// Environment configuration
const config = {
  API_BASE_URL: import.meta.env.VITE_API_BASE_URL || 'https://api.myproject.nexa',
  APP_NAME: import.meta.env.VITE_APP_NAME || 'MyProject.nexa',
  ENVIRONMENT: import.meta.env.VITE_ENVIRONMENT || 'development',
  DEBUG: import.meta.env.VITE_ENVIRONMENT === 'development',
};

export default config;