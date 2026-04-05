import apiClient from './axiosService';
import type { SoftwareAsset, CreateAssetRequest, DashboardStats } from '../types';

/**
 * Software Asset API Service Methods
 */
export const assetService = {
  // CRUD Operations
  getAllAssets: () => apiClient.get<SoftwareAsset[]>('/software-assets'),
  
  getAssetById: (id: string) => apiClient.get<SoftwareAsset>(`/software-assets/${id}`),
  
  createAsset: (data: CreateAssetRequest) => 
    apiClient.post<SoftwareAsset>('/software-assets', data),
  
  updateAsset: (id: string, data: CreateAssetRequest) => 
    apiClient.put<SoftwareAsset>(`/software-assets/${id}`, data),
  
  deleteAsset: (id: string) => apiClient.delete(`/software-assets/${id}`),

  // Specialized Queries
  getExpiredLicenses: () => apiClient.get<SoftwareAsset[]>('/software-assets/expired/licenses'),
  
  getLicensesExpiringWithin: (days: number) => 
    apiClient.get<SoftwareAsset[]>('/software-assets/expiring/within', { params: { days } }),
  
  getVulnerableAssets: () => apiClient.get<SoftwareAsset[]>('/software-assets/vulnerable'),
  
  getCriticalAssets: () => apiClient.get<SoftwareAsset[]>('/software-assets/critical'),
  
  getAssetsByVendor: (vendor: string) => 
    apiClient.get<SoftwareAsset[]>(`/software-assets/vendor/${vendor}`),
  
  searchAssets: (query: string) => 
    apiClient.get<SoftwareAsset[]>('/software-assets/search', { params: { q: query } }),

  // Dashboard Statistics
  getDashboardStats: () => apiClient.get<DashboardStats>('/software-assets/stats/dashboard'),
};

export default assetService;
