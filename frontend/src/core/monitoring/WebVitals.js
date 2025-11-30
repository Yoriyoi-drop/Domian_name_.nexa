import { onCLS, onFCP, onINP, onLCP, onTTFB } from 'web-vitals';

// Function to send web vitals to analytics service
const sendToAnalytics = ({ name, value, id }) => {
  // Send to your analytics service
  // This could be sent to a backend endpoint, Sentry, or other analytics tools
  const metricData = {
    metricName: name,
    metricValue: value,
    uniqueId: id,
    timestamp: Date.now(),
    userAgent: navigator.userAgent,
    url: window.location.href,
    environment: process.env.NODE_ENV || 'development',
  };

  // Example: Send to backend API
  if (process.env.NODE_ENV === 'production') {
    // In a real implementation, you would send this to your analytics API
    // fetch('/api/vitals', {
    //   method: 'POST',
    //   body: JSON.stringify(metricData),
    //   headers: {
    //     'Content-Type': 'application/json'
    //   }
    // });
  } else {
    // For development, log the metrics
    console.log(`Web Vitals - ${name}:`, value);
  }
};

// Initialize web vitals monitoring
export const initWebVitals = () => {
  onCLS(sendToAnalytics);
  onFCP(sendToAnalytics);
  onINP(sendToAnalytics);
  onLCP(sendToAnalytics);
  onTTFB(sendToAnalytics);
};

// Function to measure resource loading times
export const measureResourceLoadTime = () => {
  if ('performance' in window) {
    window.addEventListener('load', () => {
      setTimeout(() => {
        const resources = performance.getEntriesByType('navigation')[0];
        if (resources) {
          const resourceData = {
            url: resources.name,
            loadTime: resources.loadEventEnd - resources.loadEventStart,
            domContentLoaded: resources.domContentLoadedEventEnd - resources.domContentLoadedEventStart,
            firstPaint: resources.responseEnd - resources.requestStart,
            timestamp: Date.now(),
          };
          
          console.log('Resource timing:', resourceData);
        }
      }, 0);
    });
  }
};