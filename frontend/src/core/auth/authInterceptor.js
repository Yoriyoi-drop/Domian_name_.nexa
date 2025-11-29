// Auth interceptor setup
import axios from 'axios';
import tokenService from './tokenService';

const setupAxiosInterceptors = () => {
  // Request interceptor to add token
  axios.interceptors.request.use(
    (config) => {
      const token = tokenService.getAccessToken();
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );

  // Response interceptor to handle token refresh
  axios.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config;

      if (error.response?.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;

        try {
          const refreshToken = tokenService.getRefreshToken();
          if (!refreshToken) {
            throw new Error('No refresh token available');
          }

          // Refresh token endpoint
          const response = await axios.post('/auth/refresh', {
            refreshToken
          });

          const { accessToken } = response.data;
          tokenService.setAccessToken(accessToken);

          // Retry original request with new token
          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
          return axios(originalRequest);
        } catch (refreshError) {
          // If refresh fails, logout user
          tokenService.clearTokens();
          window.location.href = '/login';
          return Promise.reject(refreshError);
        }
      }

      return Promise.reject(error);
    }
  );
};

export default setupAxiosInterceptors;