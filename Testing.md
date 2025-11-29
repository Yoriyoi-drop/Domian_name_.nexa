# Testing Guide for MyProject.nexa

This document provides a comprehensive guide to testing the MyProject.nexa enterprise application.

## 🧪 Testing Overview

The application implements a comprehensive testing strategy that includes:

- **Unit Tests**: Component and service level testing
- **Integration Tests**: API and database integration testing
- **End-to-End Tests**: Full user workflow testing
- **Security Tests**: Authentication and authorization testing

## 📁 Test Structure

### Frontend Tests

```
frontend/
├── src/
│   ├── __tests__/          # Component and utility tests
│   │   ├── components/     # Component unit tests
│   │   ├── hooks/          # Custom hook tests
│   │   └── utils/          # Utility function tests
│   └── features/
│       ├── auth/
│       │   └── __tests__/  # Feature-specific tests
│       └── ...
```

### Backend Tests

```
backend/
├── src/test/java/
│   ├── com/myproject/nexa/
│   │   ├── controllers/    # API endpoint tests
│   │   ├── services/       # Service layer tests
│   │   ├── repositories/   # Database integration tests
│   │   └── security/       # Security tests
```

## 🧩 Unit Testing

### Frontend Unit Tests

Frontend unit tests use Jest and React Testing Library:

```bash
# Run frontend unit tests
cd frontend
npm test

# Run with coverage
npm run test:coverage

# Run specific test file
npm test LoginPage.test.jsx
```

Example unit test:

```jsx
// LoginPage.test.jsx
import { render, screen, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import LoginPage from './LoginPage';

describe('LoginPage', () => {
  test('renders login form with email and password fields', () => {
    render(
      <BrowserRouter>
        <LoginPage />
      </BrowserRouter>
    );

    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /sign in/i })).toBeInTheDocument();
  });

  test('displays validation errors for invalid inputs', async () => {
    render(
      <BrowserRouter>
        <LoginPage />
      </BrowserRouter>
    );

    fireEvent.click(screen.getByRole('button', { name: /sign in/i }));

    expect(await screen.findByText(/email is required/i)).toBeInTheDocument();
    expect(await screen.findByText(/password is required/i)).toBeInTheDocument();
  });
});
```

### Backend Unit Tests

Backend unit tests use JUnit 5 and Mockito:

```bash
# Run backend unit tests
cd backend
mvn test

# Run with coverage
mvn test jacoco:report
```

Example unit test:

```java
// AuthServiceTest.java
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void login_WithValidCredentials_ReturnsAuthResponse() {
        // Given
        LoginRequest request = new LoginRequest("testuser", "password");
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("$2a$10$mockedHashedPassword");
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "$2a$10$mockedHashedPassword")).thenReturn(true);
        when(tokenService.createRefreshToken(any(User.class))).thenReturn(new RefreshToken());

        // When
        AuthResponse response = authService.login(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getUser().getUsername()).isEqualTo("testuser");
    }
}
```

## 🔗 Integration Testing

### Frontend Integration Tests

API integration tests verify communication with the backend:

```jsx
// apiClient.test.jsx
import apiClient from '../core/api/apiClient';

describe('API Client', () => {
  test('includes auth token in requests when available', async () => {
    // Mock token
    localStorage.setItem('access_token', 'mock-token');
    
    // Mock API call
    const mockResponse = { data: 'test data' };
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve(mockResponse),
        ok: true,
      })
    );

    const response = await apiClient.get('/api/test');
    
    expect(global.fetch).toHaveBeenCalledWith(
      expect.any(String),
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: 'Bearer mock-token'
        })
      })
    );
  });
});
```

### Backend Integration Tests

Spring Boot integration tests verify full request/response cycles:

```java
// AuthControllerIntegrationTest.java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("POST /api/v1/auth/login should authenticate user and return tokens")
    void login_ValidCredentials_ReturnsTokens() throws Exception {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        userRepository.save(user);

        LoginRequest request = new LoginRequest("testuser", "password");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());
    }
}
```

## 🧬 End-to-End Testing

For comprehensive end-to-end testing, we recommend using Playwright:

```bash
# Install Playwright (if not already installed)
cd frontend
npm install --save-dev @playwright/test
npx playwright install
```

Example E2E test:

```javascript
// login.e2e.spec.js
import { test, expect } from '@playwright/test';

test.describe('Authentication Flow', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('https://myproject.nexa/login');
  });

  test('should allow user to login and access dashboard', async ({ page }) => {
    // Fill login form
    await page.locator('input[name="email"]').fill('admin@myproject.nexa');
    await page.locator('input[name="password"]').fill('admin123');
    await page.locator('button[type="submit"]').click();

    // Verify redirect to dashboard
    await expect(page).toHaveURL('https://myproject.nexa/dashboard');
    await expect(page.locator('h1')).toContainText('Dashboard');
  });

  test('should show error for invalid credentials', async ({ page }) => {
    // Fill login form with invalid credentials
    await page.locator('input[name="email"]').fill('invalid@example.com');
    await page.locator('input[name="password"]').fill('wrongpassword');
    await page.locator('button[type="submit"]').click();

    // Verify error message
    await expect(page.locator('.error')).toContainText('Invalid credentials');
  });
});
```

## 🔐 Security Testing

### Authentication Tests

```java
// SecurityControllerTest.java
@TestMethodOrder(OrderAnnotation.class)
class SecurityControllerTest {

    @Test
    @WithMockUser(roles = {"USER"})
    @DisplayName("GET /api/v1/users/me should return current user for authenticated users")
    void getCurrentUser_AuthenticatedUser_ReturnsUser() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    @DisplayName("GET /api/v1/users/me should return 401 for unauthenticated users")
    void getCurrentUser_UnauthenticatedUser_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());
    }
}
```

### API Security Tests

```java
// SecurityConfigTest.java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("API endpoints should require authentication except auth endpoints")
    void apiEndpoints_RequireAuthentication() throws Exception {
        // Test that protected endpoint requires authentication
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isUnauthorized());

        // Test that auth endpoint does not require authentication
        mockMvc.perform(post("/api/v1/auth/login"))
                .andExpect(status().is(400)); // 400 because request body is missing, not 401
    }
}
```

## 📊 Code Coverage

### Frontend Coverage

```bash
# Generate frontend code coverage
cd frontend
npm run test -- --coverage

# Output coverage to HTML
npm run test -- --coverage --coverageReporters=html
```

### Backend Coverage

```bash
# Generate backend code coverage
cd backend
mvn test jacoco:report

# Generate and open coverage report
mvn jacoco:report
open target/site/jacoco/index.html
```

## 🚀 Continuous Testing

### GitHub Actions Integration

Testing is integrated into the CI/CD pipeline:

```yaml
# .github/workflows/test.yml
name: Run Tests

on: [push, pull_request]

jobs:
  frontend-tests:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4
    - name: Setup Node.js
      uses: actions/setup-node@v3
      with:
        node-version: '18'
    - name: Install dependencies
      run: cd frontend && npm ci
    - name: Run tests
      run: cd frontend && npm test -- --coverage
    - name: Upload coverage
      uses: codecov/codecov-action@v3

  backend-tests:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: postgres
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
    steps:
    - uses: actions/checkout@v4
    - name: Setup Java
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Run tests with Maven
      run: cd backend && mvn test
    - name: Upload coverage
      uses: codecov/codecov-action@v3
```

## 🧪 Test Best Practices

### Frontend Testing Best Practices

1. **Test Components in Isolation**: Use `@testing-library/react` to test components without implementation details
2. **Mock External Dependencies**: Mock API calls, timers, and other external dependencies
3. **Test User Interactions**: Simulate real user interactions rather than testing implementation details
4. **Accessibility Testing**: Test for accessibility compliance
5. **Snapshot Testing**: Use snapshots for components that rarely change

### Backend Testing Best Practices

1. **Use Test Slices**: Use `@WebMvcTest`, `@DataJpaTest`, and `@ServiceTest` for focused testing
2. **Mock External Services**: Use `@MockBean` for external dependencies like payment services
3. **Test Security**: Verify authentication and authorization logic
4. **Database Cleanup**: Ensure tests don't affect each other with proper data cleanup
5. **Property Override**: Use test-specific property files to avoid conflicts with production settings

## 📈 Monitoring Test Results

### Local Test Execution

```bash
# Run all tests and watch for changes
npm test -- --watch  # Frontend
mvn test -Dtest=**/*Test.java  # Backend

# Run specific test suite
npm test -- src/features/auth/__tests__/
mvn test -Dtest=AuthControllerTest
```

### Test Reports

Test reports are generated in the following locations:

- Frontend: `frontend/coverage/`
- Backend: `backend/target/surefire-reports/` and `backend/target/site/jacoco/`

---

This testing guide provides a comprehensive framework for maintaining high code quality and ensuring the reliability of the MyProject.nexa application.