import React from 'react';
import Loading from './Loading';

const SuspenseFallback = () => {
  return (
    <div className="flex items-center justify-center min-h-screen">
      <Loading message="Loading application..." />
    </div>
  );
};

export default SuspenseFallback;