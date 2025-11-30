import React from 'react';
import { cn } from '@/utils/cn';

const LoadingSpinner = ({ className, size = 'md' }) => {
  const sizeClasses = {
    sm: 'w-4 h-4',
    md: 'w-8 h-8',
    lg: 'w-12 h-12',
    xl: 'w-16 h-16',
  };

  return (
    <div className="flex justify-center items-center p-8">
      <div
        className={cn(
          'animate-spin rounded-full border-4 border-gray-300 border-t-blue-500',
          sizeClasses[size],
          className
        )}
        role="status"
        aria-label="loading"
      >
        <span className="sr-only">Loading...</span>
      </div>
    </div>
  );
};

export { LoadingSpinner };