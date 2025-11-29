// API endpoints configuration
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'https://api.myproject.nexa';

export const AUTH_ENDPOINTS = {
  LOGIN: `${API_BASE_URL}/api/v1/auth/login`,
  REGISTER: `${API_BASE_URL}/api/v1/auth/register`,
  REFRESH: `${API_BASE_URL}/api/v1/auth/refresh`,
  LOGOUT: `${API_BASE_URL}/api/v1/auth/logout`,
};

export const USER_ENDPOINTS = {
  BASE: `${API_BASE_URL}/api/v1/users`,
  ME: `${API_BASE_URL}/api/v1/users/me`,
};

export const DASHBOARD_ENDPOINTS = {
  STATS: `${API_BASE_URL}/api/v1/dashboard/stats`,
  ACTIVITY: `${API_BASE_URL}/api/v1/dashboard/activity`,
};