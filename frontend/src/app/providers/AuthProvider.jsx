import React, { createContext, useContext, useState, useEffect } from 'react';
import tokenService from '@/core/auth/tokenService';
import apiClient from '@/core/api/apiClient';

const AuthContext = createContext();

export { AuthContext };

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check if user is already logged in on app start
    const storedUser = tokenService.getUser();
    if (storedUser) {
      setUser(storedUser);
    }
    setLoading(false);
  }, []);

  const login = async (credentials) => {
    try {
      const response = await apiClient.post('/api/v1/auth/login', credentials);
      const { user, accessToken, refreshToken } = response.data;

      tokenService.setAccessToken(accessToken);
      tokenService.setRefreshToken(refreshToken);
      tokenService.setUser(user);

      setUser(user);
      return { success: true, user };
    } catch (error) {
      return { 
        success: false, 
        error: error.response?.data?.message || 'Login failed' 
      };
    }
  };

  const register = async (userData) => {
    try {
      const response = await apiClient.post('/api/v1/auth/register', userData);
      const { user, accessToken, refreshToken } = response.data;

      tokenService.setAccessToken(accessToken);
      tokenService.setRefreshToken(refreshToken);
      tokenService.setUser(user);

      setUser(user);
      return { success: true, user };
    } catch (error) {
      return { 
        success: false, 
        error: error.response?.data?.message || 'Registration failed' 
      };
    }
  };

  const logout = async () => {
    try {
      // Call logout API to invalidate refresh token on server
      await apiClient.post('/api/v1/auth/logout');
    } catch (error) {
      // Continue with logout even if API call fails
      console.error('Logout API call failed:', error);
    } finally {
      // Clear all stored data
      tokenService.clearTokens();
      setUser(null);
    }
  };

  const value = {
    user,
    login,
    register,
    logout,
    loading
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};