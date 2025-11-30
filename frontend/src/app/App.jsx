import React from 'react';
import { AuthProvider } from './providers/AuthProvider';
import { ThemeProvider } from './providers/ThemeProvider';
import { ReduxProvider } from '../core/store/ReduxProvider';
import AppRoutes from './AppRoutes';
import ErrorBoundary from '../shared/components/common/ErrorBoundary';
import { initSentry } from '../core/monitoring/SentryConfig';
import { initWebVitals, measureResourceLoadTime } from '../core/monitoring/WebVitals';

// Initialize monitoring services
initSentry();
initWebVitals();
measureResourceLoadTime();

function App() {
  return (
    <ErrorBoundary>
      <ReduxProvider>
        <ThemeProvider>
          <AuthProvider>
            <AppRoutes />
          </AuthProvider>
        </ThemeProvider>
      </ReduxProvider>
    </ErrorBoundary>
  );
}

export default App;