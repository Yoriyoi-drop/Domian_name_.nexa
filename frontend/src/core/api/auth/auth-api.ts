// src/core/api/auth/auth-api.ts
import apiClient from '../apiClient';
import {
  LoginRequest,
  RegisterRequest,
  AuthResponse,
  RefreshTokenRequest,
  LogoutRequest
} from './auth-types';
import { ApiResponse } from '../common/api-types';
import API_ENDPOINTS from '../endpoints';

export const authApi = {
  login: async (credentials: LoginRequest): Promise<ApiResponse<AuthResponse>> => {
    const response = await apiClient.post(`${API_ENDPOINTS.AUTH.LOGIN}`, credentials);
    return response.data;
  },

  register: async (userData: RegisterRequest): Promise<ApiResponse<AuthResponse>> => {
    const response = await apiClient.post(`${API_ENDPOINTS.AUTH.REGISTER}`, userData);
    return response.data;
  },

  logout: async (logoutData: LogoutRequest): Promise<ApiResponse<void>> => {
    const response = await apiClient.post(`${API_ENDPOINTS.AUTH.LOGOUT}`, logoutData);
    return response.data;
  },

  refreshToken: async (refreshToken: RefreshTokenRequest): Promise<ApiResponse<AuthResponse>> => {
    const response = await apiClient.post(`${API_ENDPOINTS.AUTH.REFRESH}`, refreshToken);
    return response.data;
  }
};