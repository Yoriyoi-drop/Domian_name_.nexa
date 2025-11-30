import axios from 'axios';
import tokenService from '../auth/tokenService';

// Create axios instance with defaults
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'https://api.myproject.nexa',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // Enable cookies to be sent with requests
});

// Request interceptor to add auth token if needed
apiClient.interceptors.request.use(
  (config) => {
    // If using authorization headers (for non-HttpOnly implementation)
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

// Response interceptor to handle token refresh and errors
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // If unauthorized and not retrying, try to refresh token
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        // For HttpOnly cookies implementation, the refresh endpoint
        // would use cookies instead of requiring the refresh token in the body
        const refreshToken = tokenService.getRefreshToken();

        // Call refresh token endpoint - in a true HttpOnly cookie implementation,
        // this request would include the refresh token as an HttpOnly cookie automatically
        const response = await apiClient.post('/auth/refresh', {
          refreshToken: refreshToken || undefined // Send only if available
        });

        const { accessToken } = response.data;

        // In HttpOnly cookie implementation, new access token might come in a cookie
        // For now, we'll still handle it as before but the backend should set HttpOnly cookies
        if (accessToken) {
          tokenService.setAccessToken(accessToken);
        }

        // Retry original request with new token
        if (accessToken) {
          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        }
        return apiClient(originalRequest);
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

    // Return the original error for client-side errors
    return Promise.reject(error);
  }
);

export default apiClient;