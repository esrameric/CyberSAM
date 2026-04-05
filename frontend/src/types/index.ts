export interface SoftwareAsset {
  id: string;
  name: string;
  version: string;
  vendor: string;
  expiryDate: string; // ISO date format
  isVulnerable: boolean;
  createdAt: string;
  updatedAt: string;
  cveNotes?: string;
  description?: string;
}

export interface CreateAssetRequest {
  name: string;
  version: string;
  vendor: string;
  expiryDate: string;
  isVulnerable: boolean;
  description?: string;
  cveNotes?: string;
}

export interface DashboardStats {
  totalAssets: number;
  vulnerableCount: number;
  expiredLicensesCount: number;
  criticalAssetsCount: number;
  healthPercentage: number;
}
