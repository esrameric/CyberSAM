import React, { useState } from 'react';
import { AlertTriangle, Calendar, Trash2, Edit2, ChevronDown } from 'lucide-react';
import type { SoftwareAsset } from '../types';

interface AssetTableProps {
  assets: SoftwareAsset[];
  onDelete?: (id: string) => void;
  onEdit?: (asset: SoftwareAsset) => void;
  loading?: boolean;
}

/**
 * AssetTable Component
 * Displays software assets with vulnerability highlighting for defense industry monitoring
 */
export const AssetTable: React.FC<AssetTableProps> = ({
  assets,
  onDelete,
  onEdit,
  loading = false,
}) => {
  const [expandedId, setExpandedId] = useState<string | null>(null);

  const isExpired = (expiryDate: string) => new Date(expiryDate) < new Date();
  const daysUntilExpiry = (expiryDate: string) => {
    const today = new Date();
    const expiry = new Date(expiryDate);
    const diffTime = expiry.getTime() - today.getTime();
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  };

  const getRowClass = (asset: SoftwareAsset) => {
    if (asset.isVulnerable || isExpired(asset.expiryDate)) {
      return 'bg-red-900/10 border-l-4 border-l-red-500';
    }
    return 'bg-gray-900/50 border-l-4 border-l-gray-700';
  };

  return (
    <div className="overflow-x-auto rounded-lg border border-gray-800">
      <table className="w-full">
        <thead>
          <tr className="bg-gray-900 border-b border-gray-800">
            <th className="px-6 py-3 text-left text-xs font-semibold text-gray-400 uppercase tracking-wider">Asset Name</th>
            <th className="px-6 py-3 text-left text-xs font-semibold text-gray-400 uppercase tracking-wider">Vendor</th>
            <th className="px-6 py-3 text-left text-xs font-semibold text-gray-400 uppercase tracking-wider">Version</th>
            <th className="px-6 py-3 text-left text-xs font-semibold text-gray-400 uppercase tracking-wider">Expiry Date</th>
            <th className="px-6 py-3 text-center text-xs font-semibold text-gray-400 uppercase tracking-wider">Status</th>
            <th className="px-6 py-3 text-center text-xs font-semibold text-gray-400 uppercase tracking-wider">Actions</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-800">
          {loading ? (
            <tr>
              <td colSpan={6} className="px-6 py-8 text-center text-gray-400">
                <div className="inline-block">
                  <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-defense-accent"></div>
                </div>
              </td>
            </tr>
          ) : assets.length === 0 ? (
            <tr>
              <td colSpan={6} className="px-6 py-8 text-center text-gray-500">
                No software assets found
              </td>
            </tr>
          ) : (
            assets.map((asset) => (
              <React.Fragment key={asset.id}>
                <tr className={`${getRowClass(asset)} transition-colors hover:bg-gray-800/50 cursor-pointer`}>
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-2">
                      <button
                        onClick={() => setExpandedId(expandedId === asset.id ? null : asset.id)}
                        className="inline-flex"
                      >
                        <ChevronDown
                          className={`w-5 h-5 text-gray-500 transition-transform ${expandedId === asset.id ? 'rotate-180' : ''}`}
                        />
                      </button>
                      <span className="font-medium text-white">{asset.name}</span>
                    </div>
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-300">{asset.vendor}</td>
                  <td className="px-6 py-4 text-sm text-gray-300">{asset.version}</td>
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-2">
                      {isExpired(asset.expiryDate) ? (
                        <>
                          <AlertTriangle className="w-4 h-4 text-red-500" />
                          <span className="text-sm font-medium text-red-400">Expired</span>
                        </>
                      ) : (
                        <>
                          <Calendar className="w-4 h-4 text-gray-500" />
                          <span className="text-sm text-gray-300">
                            {new Date(asset.expiryDate).toLocaleDateString()} ({daysUntilExpiry(asset.expiryDate)} days)
                          </span>
                        </>
                      )}
                    </div>
                  </td>
                  <td className="px-6 py-4 text-center">
                    <div className="flex items-center justify-center gap-2">
                      {asset.isVulnerable && (
                        <span className="defense-badge bg-red-900 text-red-200 border border-red-700">
                          🔴 CVE
                        </span>
                      )}
                      {isExpired(asset.expiryDate) && (
                        <span className="defense-badge bg-orange-900 text-orange-200 border border-orange-700">
                          ⚠️ Expired
                        </span>
                      )}
                      {!asset.isVulnerable && !isExpired(asset.expiryDate) && (
                        <span className="defense-badge bg-green-900 text-green-200 border border-green-700">
                          ✓ Safe
                        </span>
                      )}
                    </div>
                  </td>
                  <td className="px-6 py-4 text-center">
                    <div className="flex items-center justify-center gap-2">
                      {onEdit && (
                        <button
                          onClick={() => onEdit(asset)}
                          className="p-1 hover:bg-gray-700 rounded-md transition-colors"
                          title="Edit asset"
                        >
                          <Edit2 className="w-4 h-4 text-blue-400" />
                        </button>
                      )}
                      {onDelete && (
                        <button
                          onClick={() => onDelete(asset.id)}
                          className="p-1 hover:bg-gray-700 rounded-md transition-colors"
                          title="Delete asset"
                        >
                          <Trash2 className="w-4 h-4 text-red-400" />
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
                {expandedId === asset.id && (
                  <tr className="bg-gray-900/50 border-l-4 border-l-defense-accent">
                    <td colSpan={6} className="px-6 py-4">
                      <div className="space-y-2">
                        <div>
                          <p className="text-xs text-gray-500 uppercase">Description</p>
                          <p className="text-sm text-gray-300">{asset.description || 'N/A'}</p>
                        </div>
                        {asset.cveNotes && (
                          <div>
                            <p className="text-xs text-gray-500 uppercase">CVE Notes</p>
                            <p className="text-sm text-red-400">{asset.cveNotes}</p>
                          </div>
                        )}
                        <div className="grid grid-cols-2 gap-4 pt-2">
                          <div>
                            <p className="text-xs text-gray-500 uppercase">Created</p>
                            <p className="text-sm text-gray-300">{new Date(asset.createdAt).toLocaleString()}</p>
                          </div>
                          <div>
                            <p className="text-xs text-gray-500 uppercase">Updated</p>
                            <p className="text-sm text-gray-300">{new Date(asset.updatedAt).toLocaleString()}</p>
                          </div>
                        </div>
                      </div>
                    </td>
                  </tr>
                )}
              </React.Fragment>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
};

export default AssetTable;
