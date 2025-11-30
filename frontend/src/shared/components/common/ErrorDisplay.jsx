import React from 'react';
import { AlertCircle, RotateCcw } from 'lucide-react';

const ErrorDisplay = ({ error, onRetry, retryMessage = "Try Again" }) => {
  return (
    <div className="flex flex-col items-center justify-center p-8 bg-red-50 rounded-lg border border-red-200">
      <AlertCircle className="w-12 h-12 text-red-500 mb-4" />
      <h3 className="text-lg font-medium text-red-800 mb-2">Something went wrong</h3>
      <p className="text-red-600 text-center mb-4">
        {error?.message || 'An unexpected error occurred'}
      </p>
      {onRetry && (
        <button
          onClick={onRetry}
          className="flex items-center gap-2 px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 transition-colors"
        >
          <RotateCcw className="w-4 h-4" />
          {retryMessage}
        </button>
      )}
    </div>
  );
};

export default ErrorDisplay;