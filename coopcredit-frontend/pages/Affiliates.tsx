import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Affiliate } from '../types';

const Affiliates = () => {
  const [affiliates, setAffiliates] = useState<Affiliate[]>([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState<Affiliate>({
    name: '',
    email: '',
    document: '',
    salary: 0,
    enrollmentDate: new Date().toISOString().split('T')[0]
  });

  useEffect(() => {
    fetchAffiliates();
  }, []);

  const fetchAffiliates = async () => {
    try {
      const response = await api.get('/affiliates');
      setAffiliates(response.data);
    } catch (error) {
      console.error('Error fetching affiliates', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/affiliates', formData);
      setIsModalOpen(false);
      fetchAffiliates();
      setFormData({ name: '', email: '', document: '', salary: 0, enrollmentDate: '' });
      alert('Afiliado creado exitosamente');
    } catch (error) {
      alert('Error al crear afiliado');
    }
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-bold text-gray-800">Afiliados</h2>
        <button
          onClick={() => setIsModalOpen(true)}
          className="bg-primary text-white px-4 py-2 rounded-lg hover:bg-blue-800 transition shadow-sm flex items-center gap-2"
        >
          <span>+ Nuevo Afiliado</span>
        </button>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Nombre</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Documento</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Salario</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Fecha Alta</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {loading ? (
                <tr><td colSpan={6} className="text-center py-4">Cargando...</td></tr>
              ) : affiliates.map((affiliate) => (
                <tr key={affiliate.id} className="hover:bg-gray-50 transition">
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{affiliate.id}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{affiliate.name}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{affiliate.document}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{affiliate.email}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${affiliate.salary.toLocaleString()}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{affiliate.enrollmentDate}</td>
                </tr>
              ))}
              {!loading && affiliates.length === 0 && (
                 <tr><td colSpan={6} className="text-center py-8 text-gray-400">No hay afiliados registrados</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Simple Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 p-4">
          <div className="bg-white rounded-lg shadow-xl w-full max-w-lg overflow-hidden">
            <div className="px-6 py-4 border-b flex justify-between items-center bg-gray-50">
              <h3 className="text-lg font-bold text-gray-800">Registrar Afiliado</h3>
              <button onClick={() => setIsModalOpen(false)} className="text-gray-400 hover:text-gray-600">&times;</button>
            </div>
            <form onSubmit={handleCreate} className="p-6 space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Nombre Completo</label>
                  <input
                    type="text"
                    required
                    className="w-full border rounded-md px-3 py-2 focus:ring-primary focus:border-primary outline-none"
                    value={formData.name}
                    onChange={e => setFormData({ ...formData, name: e.target.value })}
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Documento</label>
                  <input
                    type="text"
                    required
                    className="w-full border rounded-md px-3 py-2 focus:ring-primary focus:border-primary outline-none"
                    value={formData.document}
                    onChange={e => setFormData({ ...formData, document: e.target.value })}
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
                  <input
                    type="email"
                    required
                    className="w-full border rounded-md px-3 py-2 focus:ring-primary focus:border-primary outline-none"
                    value={formData.email}
                    onChange={e => setFormData({ ...formData, email: e.target.value })}
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Salario ($)</label>
                  <input
                    type="number"
                    min="1"
                    required
                    className="w-full border rounded-md px-3 py-2 focus:ring-primary focus:border-primary outline-none"
                    value={formData.salary}
                    onChange={e => setFormData({ ...formData, salary: Number(e.target.value) })}
                  />
                </div>
                <div className="md:col-span-2">
                  <label className="block text-sm font-medium text-gray-700 mb-1">Fecha de Afiliación</label>
                  <input
                    type="date"
                    required
                    max={new Date().toISOString().split('T')[0]}
                    className="w-full border rounded-md px-3 py-2 focus:ring-primary focus:border-primary outline-none"
                    value={formData.enrollmentDate}
                    onChange={e => setFormData({ ...formData, enrollmentDate: e.target.value })}
                  />
                </div>
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
                  Guardar
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Affiliates;