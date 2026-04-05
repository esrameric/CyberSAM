import React from 'react';
import { AlertTriangle, Lock, BarChart3, Zap } from 'lucide-react';

interface DashboardCardProps {
  title: string;
  value: string | number;
  icon: 'vulnerable' | 'expired' | 'critical' | 'health';
  trend?: number;
  description?: string;
}

const iconMap = {
  vulnerable: AlertTriangle,
  expired: Lock,
  critical: Zap,
  health: BarChart3,
};

const colorMap = {
  vulnerable: { bg: 'bg-red-900/20', border: 'border-red-700', icon: 'text-red-500' },
  expired: { bg: 'bg-orange-900/20', border: 'border-orange-700', icon: 'text-orange-500' },
  critical: { bg: 'bg-defense-danger/20', border: 'border-defense-danger', icon: 'text-defense-danger' },
  health: { bg: 'bg-green-900/20', border: 'border-green-700', icon: 'text-green-500' },
};

/**
 * DashboardCard Component
 * Displays key metrics for the defense industry dashboard
 */
export const DashboardCard: React.FC<DashboardCardProps> = ({
  title,
  value,
  icon,
  trend,
  description,
}) => {
  const IconComponent = iconMap[icon];
  const colors = colorMap[icon];

  return (
    <div className={`defense-card border-2 ${colors.border} p-6 transition-all hover:shadow-2xl hover:shadow-${icon === 'health' ? 'green' : icon === 'critical' ? 'red' : 'orange'}-500/20`}>
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <p className="text-sm font-medium text-gray-400 uppercase tracking-wider">{title}</p>
          <p className="text-3xl font-bold mt-2 text-white">{value}</p>
          {description && (
            <p className="text-xs text-gray-500 mt-2">{description}</p>
          )}
        </div>
        <div className={`p-3 rounded-lg ${colors.bg} border ${colors.border}`}>
          <IconComponent className={`w-6 h-6 ${colors.icon}`} />
        </div>
      </div>
      {trend !== undefined && (
        <div className="mt-4 pt-4 border-t border-gray-800">
          <span className={`text-sm font-medium ${trend > 0 ? 'text-red-500' : 'text-green-500'}`}>
            {trend > 0 ? '↑' : '↓'} {Math.abs(trend)}% from last check
          </span>
        </div>
      )}
    </div>
  );
};

export default DashboardCard;
