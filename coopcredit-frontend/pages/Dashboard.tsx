import React, { useEffect, useState } from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import api from '../services/api';
import { Affiliate, Credit, CreditStatus } from '../types';

const Dashboard = () => {
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState({
    totalAffiliates: 0,
    totalCredits: 0,
    pending: 0,
    approved: 0,
    rejected: 0
  });
  const [credits, setCredits] = useState<Credit[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [affiliatesRes, creditsRes] = await Promise.all([
          api.get<Affiliate[]>('/affiliates'),
          api.get<Credit[]>('/credits')
        ]);

        const creditList = creditsRes.data;
        const affiliateList = affiliatesRes.data;

        setCredits(creditList);
        setStats({
          totalAffiliates: affiliateList.length,
          totalCredits: creditList.length,
          pending: creditList.filter(c => c.status === CreditStatus.PENDING).length,
          approved: creditList.filter(c => c.status === CreditStatus.APPROVED).length,
          rejected: creditList.filter(c => c.status === CreditStatus.REJECTED).length,
        });
      } catch (error) {
        console.error("Failed to load dashboard data", error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const chartData = [
    { name: 'Aprobados', cantidad: stats.approved, fill: '#10b981' }, // green
    { name: 'Pendientes', cantidad: stats.pending, fill: '#f59e0b' }, // yellow
    { name: 'Rechazados', cantidad: stats.rejected, fill: '#ef4444' }, // red
  ];

  if (loading) return <div className="flex justify-center p-10"><div className="animate-spin h-8 w-8 border-4 border-primary border-t-transparent rounded-full"></div></div>;

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold text-gray-800">Dashboard</h2>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard title="Total Afiliados" value={stats.totalAffiliates} color="bg-blue-500" />
        <StatCard title="Solicitudes Totales" value={stats.totalCredits} color="bg-indigo-500" />
        <StatCard title="Pendientes" value={stats.pending} color="bg-accent" />
        <StatCard title="Créditos Aprobados" value={stats.approved} color="bg-secondary" />
      </div>

      <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
        <h3 className="text-lg font-semibold mb-4 text-gray-700">Estado de Solicitudes</h3>
        <div className="h-80 w-full">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={chartData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip cursor={{fill: 'transparent'}} />
              <Bar dataKey="cantidad" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
};

const StatCard = ({ title, value, color }: { title: string, value: number, color: string }) => (
  <div className={`${color} text-white p-6 rounded-xl shadow-lg transition transform hover:-translate-y-1`}>
    <p className="text-sm opacity-80 font-medium uppercase tracking-wide">{title}</p>
    <p className="text-3xl font-bold mt-1">{value}</p>
  </div>
);

export default Dashboard;