import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { LazyRoutes } from './LazyRoutes';

const AppRoutes = () => {
  return (
    <Routes>
      <LazyRoutes />
    </Routes>
  );
};

export default AppRoutes;