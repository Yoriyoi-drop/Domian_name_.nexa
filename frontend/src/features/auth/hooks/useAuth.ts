// src/features/auth/hooks/useAuth.ts
import { useContext, useState, useEffect } from 'react';
import { AuthContext } from '../../app/providers/AuthProvider';
import { authApi } from '../../core/api/auth/auth-api';
import { LoginRequest, RegisterRequest, AuthResponse } from '../../core/api/auth/auth-types';
import { UserResponse } from '../../core/api/user/user-types';
import tokenService from '../auth/tokenService';

interface AuthContextType {
  user: UserResponse | null;
  isAuthenticated: boolean;
  loading: boolean;
  login: (credentials: LoginRequest) => Promise<{ success: boolean; error?: string }>;
  register: (userData: RegisterRequest) => Promise<{ success: boolean; error?: string }>;
  logout: () => void;
  refreshToken: () => Promise<boolean>;
}

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};