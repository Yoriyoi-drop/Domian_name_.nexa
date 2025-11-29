import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './providers/AuthProvider';
import ProtectedRoute from '../shared/components/common/ProtectedRoute';

// Public pages
import LoginPage from '../features/auth/pages/LoginPage';
import RegisterPage from '../features/auth/pages/RegisterPage';

// Protected pages
import DashboardPage from '../features/dashboard/pages/DashboardPage';
import UsersListPage from '../features/users/pages/UsersListPage';
import UserDetailPage from '../features/users/pages/UserDetailPage';
import UserEditPage from '../features/users/pages/UserEditPage';
import MainLayout from '../shared/components/layout/MainLayout';

const AppRoutes = () => {
  const { user } = useAuth();

  return (
    <Routes>
      {/* Public routes */}
      <Route path="/login" element={!user ? <LoginPage /> : <Navigate to="/dashboard" />} />
      <Route path="/register" element={!user ? <RegisterPage /> : <Navigate to="/dashboard" />} />
      
      {/* Protected routes */}
      <Route 
        path="/" 
        element={
          user ? (
            <ProtectedRoute>
              <MainLayout>
                <Navigate to="/dashboard" />
              </MainLayout>
            </ProtectedRoute>
          ) : (
            <Navigate to="/login" />
          )
        } 
      />
      
      <Route 
        path="/dashboard" 
        element={
          user ? (
            <ProtectedRoute>
              <MainLayout>
                <DashboardPage />
              </MainLayout>
            </ProtectedRoute>
          ) : (
            <Navigate to="/login" />
          )
        } 
      />
      
      <Route 
        path="/users" 
        element={
          user ? (
            <ProtectedRoute>
              <MainLayout>
                <UsersListPage />
              </MainLayout>
            </ProtectedRoute>
          ) : (
            <Navigate to="/login" />
          )
        } 
      />
      
      <Route 
        path="/users/:id" 
        element={
          user ? (
            <ProtectedRoute>
              <MainLayout>
                <UserDetailPage />
              </MainLayout>
            </ProtectedRoute>
          ) : (
            <Navigate to="/login" />
          )
        } 
      />
      
      <Route 
        path="/users/:id/edit" 
        element={
          user ? (
            <ProtectedRoute>
              <MainLayout>
                <UserEditPage />
              </MainLayout>
            </ProtectedRoute>
          ) : (
            <Navigate to="/login" />
          )
        } 
      />
      
      {/* Catch-all route */}
      <Route path="*" element={<Navigate to="/" />} />
    </Routes>
  );
};

export default AppRoutes;