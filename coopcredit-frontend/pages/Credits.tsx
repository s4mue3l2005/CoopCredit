import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Credit, Affiliate, CreditStatus, RiskLevel } from '../types';

const Credits = () => {
  const [credits, setCredits] = useState<Credit[]>([]);
  const [affiliates, setAffiliates] = useState<Affiliate[]>([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  
  // Filter state
  const [statusFilter, setStatusFilter] = useState<string>('ALL');

  // Form State
  const [form, setForm] = useState({
    affiliateId: '',
    amount: 0,
    term: 12
  });

  // Calculate installment preview
  const monthlyInstallment = form.amount > 0 && form.term > 0 
    ? (form.amount / form.term).toFixed(2) 
    : '0.00';

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [creditsRes, affiliatesRes] = await Promise.all([
        api.get('/credits'),
        api.get('/affiliates')
      ]);
      setCredits(creditsRes.data);
      setAffiliates(affiliatesRes.data);
    } catch (error) {
      console.error('Error fetching data', error);
    } finally {
      setLoading(false);
    }
  };

  const handleRequest = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.affiliateId) return;

    try {
      await api.post('/credits', {
        affiliateId: Number(form.affiliateId),
        amount: Number(form.amount),
        term: Number(form.term)
      });
      setIsModalOpen(false);
      fetchData();
      setForm({ affiliateId: '', amount: 0, term: 12 });
      alert('Crédito solicitado correctamente');
    } catch (error) {
      alert('Error al solicitar crédito');
    }
  };

  const getStatusColor = (status: CreditStatus) => {
    switch (status) {
      case CreditStatus.APPROVED: return 'bg-secondary text-white';
      case CreditStatus.REJECTED: return 'bg-danger text-white';
      case CreditStatus.PENDING: return 'bg-accent text-white';
      default: return 'bg-gray-200 text-gray-800';
    }
  };

  const getRiskColor = (level: RiskLevel) => {
    switch (level) {
      case RiskLevel.LOW: return 'text-green-600 bg-green-100';
      case RiskLevel.MEDIUM: return 'text-yellow-600 bg-yellow-100';
      case RiskLevel.HIGH: return 'text-orange-600 bg-orange-100';
      case RiskLevel.CRITICAL: return 'text-red-600 bg-red-100';
      default: return 'text-gray-600 bg-gray-100';
    }
  };

  const filteredCredits = statusFilter === 'ALL' 
    ? credits 
    : credits.filter(c => c.status === statusFilter);

  return (
    <div>
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-6">
        <h2 className="text-2xl font-bold text-gray-800">Solicitudes de Crédito</h2>
        <div className="flex gap-3">
          <select 
            className="border rounded-lg px-3 py-2 bg-white text-gray-700 outline-none focus:ring-2 focus:ring-primary"
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
          >
            <option value="ALL">Todos los estados</option>
            <option value="PENDING">Pendientes</option>
            <option value="APPROVED">Aprobados</option>
            <option value="REJECTED">Rechazados</option>
          </select>
          <button
            onClick={() => setIsModalOpen(true)}
            className="bg-primary text-white px-4 py-2 rounded-lg hover:bg-blue-800 transition shadow-sm"
          >
            + Solicitar Crédito
          </button>
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Afiliado (ID)</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Monto</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Plazo</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Cuota</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Riesgo</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Estado</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {loading ? (
                <tr><td colSpan={7} className="text-center py-4">Cargando...</td></tr>
              ) : filteredCredits.map((credit) => (
                <tr key={credit.id} className="hover:bg-gray-50 transition">
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{credit.id}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    {/* Assuming backend might not hydrate name, using ID or lookup */}
                    {affiliates.find(a => a.id === credit.affiliateId)?.name || `#${credit.affiliateId}`}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 font-semibold">
                    ${credit.amount.toLocaleString()}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{credit.term} meses</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${credit.monthlyInstallment.toLocaleString()}</td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center gap-2">
                      <div className="w-16 bg-gray-200 rounded-full h-1.5 dark:bg-gray-700">
                        <div 
                          className={`h-1.5 rounded-full ${credit.riskScore > 700 ? 'bg-green-500' : credit.riskScore > 500 ? 'bg-yellow-500' : 'bg-red-500'}`} 
                          style={{ width: `${(credit.riskScore / 1000) * 100}%` }}
                        ></div>
                      </div>
                      <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${getRiskColor(credit.riskLevel)}`}>
                        {credit.riskLevel}
                      </span>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getStatusColor(credit.status)}`}>
                      {credit.status}
                    </span>
                  </td>
                </tr>
              ))}
              {!loading && filteredCredits.length === 0 && (
                 <tr><td colSpan={7} className="text-center py-8 text-gray-400">No hay créditos encontrados</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Credit Request Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 p-4">
          <div className="bg-white rounded-lg shadow-xl w-full max-w-lg overflow-hidden">
            <div className="px-6 py-4 border-b flex justify-between items-center bg-gray-50">
              <h3 className="text-lg font-bold text-gray-800">Solicitar Crédito</h3>
              <button onClick={() => setIsModalOpen(false)} className="text-gray-400 hover:text-gray-600">&times;</button>
            </div>
            <form onSubmit={handleRequest} className="p-6 space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Afiliado</label>
                <select
                  required
                  className="w-full border rounded-md px-3 py-2 focus:ring-primary focus:border-primary outline-none bg-white"
                  value={form.affiliateId}
                  onChange={e => setForm({ ...form, affiliateId: e.target.value })}
                >
                  <option value="">Seleccione un afiliado</option>
                  {affiliates.map(aff => (
                    <option key={aff.id} value={aff.id}>{aff.name} (Doc: {aff.document})</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Monto ($)</label>
                <input
                  type="number"
                  min="1"
                  required
                  className="w-full border rounded-md px-3 py-2 focus:ring-primary focus:border-primary outline-none"
                  value={form.amount}
                  onChange={e => setForm({ ...form, amount: Number(e.target.value) })}
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Plazo (Meses)</label>
                <select
                  required
                  className="w-full border rounded-md px-3 py-2 focus:ring-primary focus:border-primary outline-none bg-white"
                  value={form.term}
                  onChange={e => setForm({ ...form, term: Number(e.target.value) })}
                >
                  {[12, 24, 36, 48, 60].map(m => (
                    <option key={m} value={m}>{m} meses</option>
                  ))}
                </select>
              </div>

              <div className="bg-blue-50 p-4 rounded-lg flex justify-between items-center mt-4">
                <span className="text-sm text-blue-800 font-medium">Cuota mensual estimada:</span>
                <span className="text-xl font-bold text-blue-900">${Number(monthlyInstallment).toLocaleString()}</span>
              </div>

              <div className="flex justify-end gap-3 mt-6">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 border rounded-md text-gray-600 hover:bg-gray-50"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-primary text-white rounded-md hover:bg-blue-800"
                >
                  Solicitar
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Credits;