# MyProject.nexa Coding Standards and Best Practices

## Table of Contents
1. [Java Coding Standards](#java-coding-standards)
2. [JavaScript/React Coding Standards](#javascriptreact-coding-standards)
3. [Git Workflow and Conventions](#git-workflow-and-conventions)
4. [Code Review Guidelines](#code-review-guidelines)
5. [Security Best Practices](#security-best-practices)
6. [Performance Best Practices](#performance-best-practices)
7. [Testing Best Practices](#testing-best-practices)

## Java Coding Standards

### Naming Conventions
- **Classes**: PascalCase (e.g., `UserService`, `UserRepository`)
- **Methods**: camelCase (e.g., `getUserById`, `updateUser`)
- **Variables**: camelCase (e.g., `userName`, `userCount`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_LOGIN_ATTEMPTS`, `DEFAULT_PAGE_SIZE`)
- **Packages**: lowercase (e.g., `com.myproject.nexa.services`)

### Code Formatting
- Use 4 spaces for indentation (no tabs)
- Maximum line length: 120 characters
- Use standard Spring Boot formatting
- Always use braces for if/for/while statements, even for single-line blocks

### Class Structure
```java
public class UserServiceImpl extends BaseServiceImpl<User, Long, UserResponse, UserRepository> 
    implements UserService {

    // 1. Static constants
    private static final String DEFAULT_ROLE = "USER";
    
    // 2. Static fields
    
    // 3. Instance variables
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    // 4. Constructors
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    // 5. Public methods
    @Override
    public UserResponse getUserById(Long id) {
        // Implementation
    }
    
    // 6. Private methods
    private void validateUser(User user) {
        // Implementation
    }
}
```

### Imports
- Order: Java standard library, third-party libraries, own code
- No wildcard imports
- Group imports by source

### Javadoc Standards
- Public methods must have Javadoc
- Classes should have Javadoc
- Use proper tags (@param, @return, @throws)

```java
/**
 * Gets user by ID.
 * 
 * @param id the user ID
 * @return UserResponse object
 * @throws ResourceNotFoundException if user not found
 */
public UserResponse getUserById(Long id) {
    // Implementation
}
```

### Spring Boot Best Practices
- Use dependency injection rather than direct instantiation
- Annotate services with `@Service`, repositories with `@Repository`, controllers with `@Controller`
- Use `@Valid` and `@Validated` for input validation
- Implement proper exception handling with `@ControllerAdvice`
- Use `@Transactional` appropriately

### Error Handling
- Create custom exceptions that extend appropriate base classes
- Use specific exception types for different scenarios
- Log errors appropriately while not exposing sensitive information

## JavaScript/React Coding Standards

### Naming Conventions
- **Components**: PascalCase (e.g., `UserCard`, `LoginForm`)
- **Functions**: camelCase (e.g., `handleClick`, `validateForm`)
- **Variables**: camelCase (e.g., `userList`, `isLoading`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `API_BASE_URL`, `MAX_FILE_SIZE`)
- **Files**: PascalCase for components, camelCase for utilities

### Code Formatting
- Use 2 spaces for indentation
- Maximum line length: 100 characters
- Use Prettier for automatic formatting
- Use semicolons

### Component Structure
```javascript
import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Button } from '@/shared/components/ui/Button';

const UserProfile = ({ userId }) => {
  // 1. State declarations
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  
  // 2. Hooks
  useEffect(() => {
    fetchUser();
  }, [userId]);
  
  // 3. Handler functions
  const handleUpdate = (userData) => {
    // Implementation
  };
  
  // 4. Helper functions
  const formatName = (firstName, lastName) => {
    return `${firstName} ${lastName}`;
  };
  
  // 5. Render method/JSX
  if (loading) return <div>Loading...</div>;
  
  return (
    <div className="user-profile">
      <h1>{formatName(user.firstName, user.lastName)}</h1>
      <Button onClick={() => handleUpdate(user)}>
        Update Profile
      </Button>
    </div>
  );
};

export default UserProfile;
```

### Error Handling
- Use try/catch blocks for asynchronous operations
- Implement proper error boundaries for React components
- Use centralized error handling

### React Best Practices
- Use functional components with hooks
- Implement proper state management (Redux Toolkit for global state)
- Use React.memo for performance optimization
- Implement proper cleanup in useEffect
- Use TypeScript for type safety

### File Structure
Use feature-sliced design approach:
```
src/
├── app/           # App configuration
├── entities/      # Business entities
├── features/      # Feature modules
├── shared/        # Shared components, utils, libs
├── widgets/       # UI widgets
└── pages/         # Page components
```

## Git Workflow and Conventions

### Branch Naming
- `feature/short-description` - New features
- `fix/short-description` - Bug fixes
- `enhancement/short-description` - Improvements
- `release/vX.Y.Z` - Release branches
- `hotfix/short-description` - Urgent fixes

### Commit Message Format
Use conventional commits format:
```
<type>(<scope>): <short summary>

<body - optional>

<footer - optional>
```

**Types:**
- feat: A new feature
- fix: A bug fix
- docs: Documentation only changes
- style: Changes that do not affect the meaning of the code
- refactor: A code change that neither fixes a bug nor adds a feature
- test: Adding missing tests or correcting existing tests
- chore: Other changes that don't modify src or test files

**Examples:**
```
feat(auth): add JWT refresh token mechanism

- Implement token refresh functionality
- Add middleware to handle expired tokens
- Update authentication service

Closes #123
```

```
fix(api): resolve pagination issue for user list

- Fix incorrect offset calculation
- Correct page number handling
- Update unit tests for pagination
```

### Pull Request Guidelines
- One logical change per pull request
- Include relevant tests
- Update documentation if needed
- Use descriptive titles and descriptions
- Link relevant issues using keywords (closes #123)

## Code Review Guidelines

### Review Checklist
- [ ] Code follows established standards
- [ ] Proper error handling implemented
- [ ] Security considerations addressed
- [ ] Performance implications considered
- [ ] Tests cover the changes
- [ ] Documentation updated if needed
- [ ] No unnecessary code or debug statements
- [ ] Database changes have migration scripts
- [ ] API changes documented

### Review Process
1. **Initial Review**: Automated checks (linting, tests, security)
2. **Peer Review**: At least one team member reviews the code
3. **Approval**: PR can be merged after required approvals
4. **For complex changes**: Additional reviews by senior developers

### Review Expectations
- Be constructive and respectful
- Focus on code quality and requirements
- Suggest improvements rather than just pointing out issues
- Ask questions if something is unclear
- Provide specific feedback with examples

## Security Best Practices

### Input Validation
- Always validate and sanitize user inputs
- Use parameterized queries to prevent SQL injection
- Implement proper CSRF protection
- Use output encoding to prevent XSS

### Authentication and Authorization
- Use strong password policies (BCrypt with strength 12)
- Implement proper session management
- Use JWT tokens with appropriate expiration times
- Apply role-based access control (RBAC)

### Data Protection
- Encrypt sensitive data at rest and in transit
- Use HTTPS for all communications
- Implement proper secrets management
- Follow data privacy regulations (GDPR, etc.)

### Error Handling
- Don't expose system details in error messages
- Log errors securely with proper access controls
- Use generic error messages for users

## Performance Best Practices

### Backend
- Implement proper caching (Redis, in-memory)
- Use pagination for large datasets
- Optimize database queries with proper indexing
- Use connection pooling efficiently
- Implement async processing for long-running operations
- Use projection to limit data retrieval

### Frontend
- Implement code splitting and lazy loading
- Optimize bundle size
- Use React.memo and useMemo appropriately
- Implement virtualization for large lists
- Use proper image optimization

### Database
- Optimize queries with proper indexing
- Use database connection pooling
- Implement read replicas for read-heavy operations
- Use appropriate data types

## Testing Best Practices

### Backend Testing
- Unit tests: Test individual methods and classes
- Integration tests: Test components with external dependencies
- Contract tests: Test API contracts
- Security tests: Test security configurations
- Performance tests: Test under load

### Frontend Testing
- Unit tests: Test components and utility functions
- Integration tests: Test component interactions
- E2E tests: Test user workflows
- Accessibility tests: Test for accessibility compliance

### Test Coverage
- Aim for at least 80% test coverage
- Focus on critical business logic
- Test edge cases and error conditions
- Maintain high coverage for security-related code

### Test Organization
- Use descriptive test names
- Follow given-when-then pattern
- Isolate tests from each other
- Use appropriate test data

**Example:**
```java
@Test
void shouldReturnUserWhenValidIdProvided() {
    // Given
    Long userId = 1L;
    User expectedUser = User.builder().id(userId).username("testuser").build();
    when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
    
    // When
    UserResponse result = userService.getUserById(userId);
    
    // Then
    assertThat(result.getId()).isEqualTo(userId);
    assertThat(result.getUsername()).isEqualTo("testuser");
    verify(userRepository, times(1)).findById(userId);
}
```

## Documentation Standards

### Code Documentation
- Document public APIs and complex logic
- Use JavaDoc/JavaDoc for public methods
- Include examples where appropriate
- Keep documentation up to date with code changes

### Architecture Documentation
- Update Architecture Decision Records (ADRs) for significant changes
- Document API endpoints with OpenAPI
- Maintain system architecture diagrams
- Document deployment procedures