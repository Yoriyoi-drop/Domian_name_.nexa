// src/core/api/user/user-api.ts
import apiClient from '../apiClient';
import {
  UserCreateRequest,
  UserUpdateRequest,
  UserResponse,
  PaginatedUsersResponse
} from './user-types';
import { ApiResponse, PaginatedResponse as GenericPaginatedResponse } from '../common/api-types';
import API_ENDPOINTS from '../endpoints';

export const userApi = {
  getCurrentUser: async (): Promise<ApiResponse<UserResponse>> => {
    const response = await apiClient.get(`${API_ENDPOINTS.USERS.ME}`);
    return response.data;
  },

  getUserById: async (id: number): Promise<ApiResponse<UserResponse>> => {
    const response = await apiClient.get(`${API_ENDPOINTS.USERS.BASE}/${id}`);
    return response.data;
  },

  getAllUsers: async (page: number = 0, size: number = 10, sortBy: string = 'id', sortDir: string = 'asc'): Promise<ApiResponse<GenericPaginatedResponse<UserResponse>>> => {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      sortBy,
      sortDir
    });
    const response = await apiClient.get(`${API_ENDPOINTS.USERS.BASE}?${params.toString()}`);
    return response.data;
  },

  createUser: async (userData: UserCreateRequest): Promise<ApiResponse<UserResponse>> => {
    const response = await apiClient.post(API_ENDPOINTS.USERS.BASE, userData);
    return response.data;
  },

  updateUser: async (id: number, userData: UserUpdateRequest): Promise<ApiResponse<UserResponse>> => {
    const response = await apiClient.put(`${API_ENDPOINTS.USERS.BASE}/${id}`, userData);
    return response.data;
  },

  deleteUser: async (id: number): Promise<ApiResponse<void>> => {
    const response = await apiClient.delete(`${API_ENDPOINTS.USERS.BASE}/${id}`);
    return response.data;
  },

  enableUser: async (id: number): Promise<ApiResponse<UserResponse>> => {
    const response = await apiClient.patch(API_ENDPOINTS.USERS.ENABLE(id));
    return response.data;
  },

  disableUser: async (id: number): Promise<ApiResponse<UserResponse>> => {
    const response = await apiClient.patch(API_ENDPOINTS.USERS.DISABLE(id));
    return response.data;
  }
};