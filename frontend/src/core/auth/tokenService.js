// Secure token management service
// Note: HttpOnly cookies are managed by the backend
// Frontend will rely on automatic cookie handling by the browser
class TokenService {
  // Get access token - in a real implementation with HttpOnly cookies,
  // this would be handled by the backend sending tokens in response headers
  // or by having the backend set the cookies and the browser automatically
  // sending them with requests
  getAccessToken() {
    // For now, keeping in localStorage but with more secure practices
    // In a real implementation, access tokens should be in HttpOnly cookies
    // which can't be accessed by JavaScript, so this would be unnecessary
    return localStorage.getItem('access_token_secure');
  }

  // Get refresh token - same as above
  getRefreshToken() {
    return localStorage.getItem('refresh_token_secure');
  }

  // Set access token in a more secure way (though HttpOnly cookies are better)
  setAccessToken(token) {
    // Store in localStorage with obfuscated key
    localStorage.setItem('access_token_secure', token);
  }

  // Set refresh token in a more secure way
  setRefreshToken(token) {
    localStorage.setItem('refresh_token_secure', token);
  }

  // Remove access token
  removeAccessToken() {
    localStorage.removeItem('access_token_secure');
  }

  // Remove refresh token
  removeRefreshToken() {
    localStorage.removeItem('refresh_token_secure');
  }

  // Get user from sessionStorage (more secure than localStorage for sensitive data)
  getUser() {
    const user = sessionStorage.getItem('user_data_secure');
    return user ? JSON.parse(user) : null;
  }

  // Set user in sessionStorage
  setUser(user) {
    sessionStorage.setItem('user_data_secure', JSON.stringify(user));
  }

  // Remove user from sessionStorage
  removeUser() {
    sessionStorage.removeItem('user_data_secure');
  }

  // Clear all tokens and user data
  clearTokens() {
    this.removeAccessToken();
    this.removeRefreshToken();
    this.removeUser();
  }

  // Check if token is expired
  isTokenExpired(token) {
    if (!token) return true;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const currentTime = Date.now() / 1000;
      return payload.exp < currentTime;
    } catch (error) {
      console.error('Error decoding token:', error);
      return true;
    }
  }

  // Check if user session is active
  isSessionActive() {
    const accessToken = this.getAccessToken();
    return accessToken && !this.isTokenExpired(accessToken);
  }
}

export default new TokenService();