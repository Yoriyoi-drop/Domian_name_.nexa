// Auth interceptor setup for cookie-based authentication
import axios from 'axios';
import tokenService from './tokenService';

const setupAxiosInterceptors = () => {
  // Configure axios to send cookies with requests
  axios.defaults.withCredentials = true;

  // Request interceptor - no need to manually add token if using cookies
  axios.interceptors.request.use(
    (config) => {
      // In cookie-based auth, the browser automatically sends cookies
      // If you still need to send token in header for some endpoints, you can do:
      const token = tokenService.getAccessToken();
      if (token && config.url.includes('/api')) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );

  // Response interceptor to handle token refresh and security
  axios.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config;

      if (error.response?.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;

        try {
          // In cookie-based implementation, refresh token might be sent via cookie
          // The backend will read it from the cookie automatically
          const response = await axios.post('/auth/refresh');

          // The new tokens will come as cookies automatically in subsequent requests
          // No need to manually handle them in the frontend
          if (response.data?.success) {
            return axios(originalRequest);
          }
        } catch (refreshError) {
          // If refresh fails, clear tokens and redirect to login
          tokenService.clearTokens();
          window.location.href = '/login';
          // Return a more user-friendly error
          return Promise.reject(new Error('Session expired. Please login again.'));
        }
      }

      // Handle other error cases
      if (error.response?.status >= 500) {
        // Server error - log for debugging but don't expose details to user
        console.error('Server error:', error.response?.data || error.message);
        return Promise.reject(new Error('Server error. Please try again later.'));
      }

      return Promise.reject(error);
    }
  );
};

export default setupAxiosInterceptors;