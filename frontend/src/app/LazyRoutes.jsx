import { lazy, Suspense } from 'react';
import { Navigate, Route } from 'react-router-dom';
import { useAuth } from './providers/AuthProvider';
import ProtectedRoute from '../shared/components/common/ProtectedRoute';
import MainLayout from '../shared/components/layout/MainLayout';
import { LoadingSpinner } from '../shared/components/ui/LoadingSpinner';

// Public pages
const LoginPage = lazy(() => import('../features/auth/pages/LoginPage'));
const RegisterPage = lazy(() => import('../features/auth/pages/RegisterPage'));

// Protected pages
const DashboardPage = lazy(() => import('../features/dashboard/pages/DashboardPage'));
const UsersListPage = lazy(() => import('../features/users/pages/UsersListPage'));
const UserDetailPage = lazy(() => import('../features/users/pages/UserDetailPage'));
const UserEditPage = lazy(() => import('../features/users/pages/UserEditPage'));
// Domain identity pages
const DomainIdentityChecker = lazy(() => import('../features/domain-identity/DomainIdentityChecker'));
const SovereignIdentityDashboard = lazy(() => import('../features/domain-identity/SovereignIdentityDashboard'));
const UniversalScopeDashboard = lazy(() => import('../features/domain-identity/UniversalScopeDashboard'));
const MonitoringDashboard = lazy(() => import('../features/domain-identity/MonitoringDashboard'));
const EcosystemDashboard = lazy(() => import('../features/domain-identity/EcosystemDashboard'));

const SuspenseWrapper = ({ children }) => (
  <Suspense fallback={<LoadingSpinner />}>
    {children}
  </Suspense>
);

export const LazyRoutes = () => {
  const { user } = useAuth();

  return (
    <>
      {/* Public routes */}
      <Route
        path="/login"
        element={
          !user ? (
            <SuspenseWrapper>
              <LoginPage />
            </SuspenseWrapper>
          ) : (
            <Navigate to="/dashboard" />
          )
        }
      />
      <Route
        path="/register"
        element={
          !user ? (
            <SuspenseWrapper>
              <RegisterPage />
            </SuspenseWrapper>
          ) : (
            <Navigate to="/dashboard" />
          )
        }
      />

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
                <SuspenseWrapper>
                  <DashboardPage />
                </SuspenseWrapper>
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
                <SuspenseWrapper>
                  <UsersListPage />
                </SuspenseWrapper>
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
                <SuspenseWrapper>
                  <UserDetailPage />
                </SuspenseWrapper>
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
                <SuspenseWrapper>
                  <UserEditPage />
                </SuspenseWrapper>
              </MainLayout>
            </ProtectedRoute>
          ) : (
            <Navigate to="/login" />
          )
        }
      />

      <Route
        path="/domain-identity"
        element={
          user ? (
            <ProtectedRoute>
              <MainLayout>
                <SuspenseWrapper>
                  <DomainIdentityChecker />
                </SuspenseWrapper>
              </MainLayout>
            </ProtectedRoute>
          ) : (
            <Navigate to="/login" />
          )
        }
      />

      <Route
        path="/sovereign-identity"
        element={
          user ? (
            <ProtectedRoute>
              <MainLayout>
                <SuspenseWrapper>
                  <SovereignIdentityDashboard />
                </SuspenseWrapper>
              </MainLayout>
            </ProtectedRoute>
          ) : (
            <Navigate to="/login" />
          )
        }
      />

      <Route
        path="/universal-scope"
        element={
          user ? (
            <ProtectedRoute>
              <MainLayout>
                <SuspenseWrapper>
                  <UniversalScopeDashboard />
                </SuspenseWrapper>
              </MainLayout>
            </ProtectedRoute>
          ) : (
            <Navigate to="/login" />
          )
        }
      />

      <Route
        path="/monitoring"
        element={
          user ? (
            <ProtectedRoute>
              <MainLayout>
                <SuspenseWrapper>
                  <MonitoringDashboard />
                </SuspenseWrapper>
              </MainLayout>
            </ProtectedRoute>
          ) : (
            <Navigate to="/login" />
          )
        }
      />

      <Route
        path="/ecosystem"
        element={
          user ? (
            <ProtectedRoute>
              <MainLayout>
                <SuspenseWrapper>
                  <EcosystemDashboard />
                </SuspenseWrapper>
              </MainLayout>
            </ProtectedRoute>
          ) : (
            <Navigate to="/login" />
          )
        }
      />

      {/* Catch-all route */}
      <Route path="*" element={<Navigate to="/" />} />
    </>
  );
};