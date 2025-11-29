// Token management service
class TokenService {
  getAccessToken() {
    return localStorage.getItem('access_token');
  }

  getRefreshToken() {
    return localStorage.getItem('refresh_token');
  }

  setAccessToken(token) {
    localStorage.setItem('access_token', token);
  }

  setRefreshToken(token) {
    localStorage.setItem('refresh_token', token);
  }

  removeAccessToken() {
    localStorage.removeItem('access_token');
  }

  removeRefreshToken() {
    localStorage.removeItem('refresh_token');
  }

  getUser() {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  setUser(user) {
    localStorage.setItem('user', JSON.stringify(user));
  }

  removeUser() {
    localStorage.removeItem('user');
  }

  clearTokens() {
    this.removeAccessToken();
    this.removeRefreshToken();
    this.removeUser();
  }

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
}

export default new TokenService();