import React, { useEffect, useState } from 'react';
import { Shield, RefreshCw, Plus, AlertTriangle } from 'lucide-react';
import DashboardCard from '../components/DashboardCard';
import AssetTable from '../components/AssetTable';
import assetService from '../services/assetService';
import type { SoftwareAsset, DashboardStats } from '../types';

/**
 * Dashboard Page - Main interface for CyberSAM
 * Defense industry standard software asset management dashboard
 */
export const Dashboard: React.FC = () => {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [assets, setAssets] = useState<SoftwareAsset[]>([]);
  const [criticalAssets, setCriticalAssets] = useState<SoftwareAsset[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadDashboard = async () => {
    try {
      setLoading(true);
      setError(null);
      
      const [statsRes, assetsRes, criticalRes] = await Promise.all([
        assetService.getDashboardStats(),
        assetService.getAllAssets(),
        assetService.getCriticalAssets(),
      ]);

      setStats(statsRes.data);
      setAssets(assetsRes.data);
      setCriticalAssets(criticalRes.data);
    } catch (err) {
      setError('Failed to load dashboard data. Please check your connection.');
      console.error('Dashboard load error:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDashboard();
    const interval = setInterval(loadDashboard, 60000); // Refresh every minute
    return () => clearInterval(interval);
  }, []);

  const handleRefresh = () => {
    loadDashboard();
  };

  const handleDelete = async (id: string) => {
    if (window.confirm('Are you sure you want to delete this asset?')) {
      try {
        await assetService.deleteAsset(id);
        loadDashboard();
      } catch (err) {
        console.error('Delete error:', err);
        setError('Failed to delete asset');
      }
    }
  };

  return (
    <div className="min-h-screen bg-defense-dark">
      {/* Header */}
      <header className="border-b border-gray-800 bg-defense-darker sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Shield className="w-8 h-8 text-defense-accent" />
              <div>
                <h1 className="text-2xl font-bold text-white">CyberSAM</h1>
                <p className="text-xs text-gray-500">Secure Software Asset Management</p>
              </div>
            </div>
            <button
              onClick={handleRefresh}
              disabled={loading}
              className="flex items-center gap-2 px-4 py-2 bg-defense-accent hover:bg-defense-accent/80 text-white rounded-lg disabled:opacity-50 transition-colors"
            >
              <RefreshCw className={`w-5 h-5 ${loading ? 'animate-spin' : ''}`} />
              Refresh
            </button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Error Alert */}
        {error && (
          <div className="mb-6 p-4 bg-red-900/20 border border-red-700 rounded-lg flex items-start gap-3">
            <AlertTriangle className="w-5 h-5 text-red-500 flex-shrink-0 mt-0.5" />
            <div>
              <h3 className="font-semibold text-red-400">Error</h3>
              <p className="text-sm text-red-300">{error}</p>
            </div>
          </div>
        )}

        {/* Dashboard Statistics */}
        {stats && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
            <DashboardCard
              title="Total Assets"
              value={stats.totalAssets}
              icon="health"
              description="Registered software items"
            />
            <DashboardCard
              title="Vulnerable"
              value={stats.vulnerableCount}
              icon="vulnerable"
              description="Assets with CVE flags"
            />
            <DashboardCard
              title="Expired Licenses"
              value={stats.expiredLicensesCount}
              icon="expired"
              description="License agreements expired"
            />
            <DashboardCard
              title="System Health"
              value={`${stats.healthPercentage}%`}
              icon="health"
              description="Overall infrastructure status"
            />
          </div>
        )}

        {/* Critical Assets Section */}
        {criticalAssets.length > 0 && (
          <div className="mb-8">
            <div className="bg-red-900/10 border border-red-700 rounded-lg p-4 flex items-start gap-3">
              <AlertTriangle className="w-6 h-6 text-red-500 flex-shrink-0 mt-0.5" />
              <div>
                <h2 className="font-semibold text-red-400 mb-2">🔴 Critical Assets Requiring Attention</h2>
                <p className="text-sm text-red-300 mb-3">
                  {criticalAssets.length} asset(s) need immediate action - either vulnerability patching or license renewal.
                </p>
                <button
                  onClick={() => document.querySelector('#critical-assets')?.scrollIntoView({ behavior: 'smooth' })}
                  className="text-sm px-3 py-1 bg-red-700 hover:bg-red-600 text-red-100 rounded transition-colors"
                >
                  View Critical Assets →
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Assets Table */}
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-bold text-white flex items-center gap-2">
              All Software Assets
              {assets.length > 0 && (
                <span className="text-sm bg-gray-800 text-gray-300 px-2 py-1 rounded">
                  {assets.length}
                </span>
              )}
            </h2>
          </div>

          <div id="critical-assets">
            <AssetTable
              assets={criticalAssets.length > 0 ? criticalAssets : assets}
              onDelete={handleDelete}
              loading={loading}
            />
          </div>

          {assets.length > 0 && criticalAssets.length > 0 && (
            <div className="mt-8 space-y-4">
              <h3 className="text-lg font-bold text-gray-300">All Assets</h3>
              <AssetTable
                assets={assets}
                onDelete={handleDelete}
                loading={loading}
              />
            </div>
          )}
        </div>
      </main>

      {/* Footer */}
      <footer className="border-t border-gray-800 bg-defense-darker mt-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 text-center text-sm text-gray-500">
          <p>CyberSAM v1.0.0 | Defense Industry Standard | © 2026</p>
        </div>
      </footer>
    </div>
  );
};

export default Dashboard;
