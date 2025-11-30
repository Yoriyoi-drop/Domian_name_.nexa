import { useEffect, useRef } from 'react';

// Custom hook for component performance monitoring
export const useComponentPerformance = (componentName) => {
  const renderCount = useRef(0);
  const startTime = useRef(performance.now());

  useEffect(() => {
    renderCount.current += 1;
    const renderTime = performance.now() - startTime.current;
    
    if (process.env.NODE_ENV === 'development' || process.env.REACT_APP_MONITORING_ENABLED === 'true') {
      console.log(`Component ${componentName} render count: ${renderCount.current}, render time: ${renderTime}ms`);
    }
  });

  // Function to measure function execution time
  const measureFunction = async (fn, fnName = 'anonymous') => {
    const start = performance.now();
    try {
      const result = await Promise.resolve(fn());
      const end = performance.now();
      
      if (process.env.NODE_ENV === 'development' || process.env.REACT_APP_MONITORING_ENABLED === 'true') {
        console.log(`Function ${fnName} executed in ${end - start}ms`);
      }
      
      return result;
    } catch (error) {
      const end = performance.now();
      if (process.env.NODE_ENV === 'development' || process.env.REACT_APP_MONITORING_ENABLED === 'true') {
        console.error(`Function ${fnName} failed after ${end - start}ms:`, error);
      }
      throw error;
    }
  };

  return { measureFunction };
};

// Hook to monitor user interactions
export const useInteractionMonitoring = () => {
  const trackClick = (elementName, additionalData = {}) => {
    if (process.env.NODE_ENV === 'production') {
      // In a real implementation, you would send this to your analytics service
      const interactionData = {
        type: 'click',
        element: elementName,
        timestamp: Date.now(),
        page: window.location.pathname,
        ...additionalData,
      };
      
      console.log('Interaction tracked:', interactionData);
    }
  };

  const trackPageView = (pageName) => {
    if (process.env.NODE_ENV === 'production') {
      const pageViewData = {
        type: 'page_view',
        page: pageName,
        timestamp: Date.now(),
        userAgent: navigator.userAgent,
      };
      
      console.log('Page view tracked:', pageViewData);
    }
  };

  return { trackClick, trackPageView };
};