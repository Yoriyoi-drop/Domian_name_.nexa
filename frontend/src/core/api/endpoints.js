// API endpoints configuration
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'https://api.myproject.nexa';

export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: '/api/v1/auth/login',
    REGISTER: '/api/v1/auth/register',
    REFRESH: '/api/v1/auth/refresh',
    LOGOUT: '/api/v1/auth/logout',
  },
  USERS: {
    BASE: '/api/v1/users',
    ME: '/api/v1/users/me',
    ENABLE: (id) => `/api/v1/users/${id}/enable`,
    DISABLE: (id) => `/api/v1/users/${id}/disable`,
  },
  DASHBOARD: {
    STATS: '/api/v1/dashboard/stats',
    ACTIVITY: '/api/v1/dashboard/activity',
  }
};

export default API_ENDPOINTS;