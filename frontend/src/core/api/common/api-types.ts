// src/core/api/common/api-types.ts
export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data?: T;
  errors?: string[];
  errorCode?: string;
  timestamp?: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalPages: number;
  totalElements: number;
  first: boolean;
  last: boolean;
}

export interface ApiError {
  message: string;
  errorCode?: string;
  errors?: string[];
  timestamp?: string;
  path?: string;
}