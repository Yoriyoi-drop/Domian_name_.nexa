import * as Sentry from '@sentry/react';
import { Integrations } from '@sentry/tracing';

const SENTRY_DSN = process.env.REACT_APP_SENTRY_DSN || '';

export const initSentry = () => {
  if (SENTRY_DSN) {
    Sentry.init({
      dsn: SENTRY_DSN,
      integrations: [
        new Integrations.BrowserTracing(),
      ],
      tracesSampleRate: 0.7, // Capture 70% of transactions
      environment: process.env.NODE_ENV || 'development',
      release: process.env.REACT_APP_VERSION || '1.0.0',
      attachStacktrace: true,
      normalizeDepth: 10, // Increase depth of object normalization
    });
  }
};

export const captureException = (error, errorInfo = null) => {
  if (SENTRY_DSN) {
    Sentry.captureException(error, {
      contexts: errorInfo ? { errorInfo } : undefined,
    });
  } else {
    console.error('Sentry error capture:', error, errorInfo);
  }
};

export const captureMessage = (message, level = 'info') => {
  if (SENTRY_DSN) {
    Sentry.captureMessage(message, level);
  } else {
    console.log(`Sentry message capture (${level}):`, message);
  }
};