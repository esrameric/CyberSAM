import axios, { AxiosInstance } from 'axios';

// Detect API URL based on environment
const getApiUrl = () => {
  // Build-time environment variable (set during Docker build)
  if (import.meta.env.VITE_API_URL) {
    return import.meta.env.VITE_API_URL;
  }
  
  // Use current window location for production (works with Nginx proxy)
  if (window.location.hostname !== 'localhost') {
    return `${window.location.protocol}//${window.location.host}/api/v1`;
  }
  
  // Development fallback
  return 'http://localhost:9595/api/v1';
};

const API_BASE_URL = getApiUrl();

/**
 * Axios API Service Configuration
 * Handles all HTTP communications with CyberSAM backend
 */
const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
});

// Request Interceptor
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    console.error('Request error:', error);
    return Promise.reject(error);
  }
);

// Response Interceptor
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('auth_token');
      window.location.href = '/login';
    }
    console.error('Response error:', error);
    return Promise.reject(error);
  }
);

export default apiClient;
