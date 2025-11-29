# MyProject.nexa Backend

This is the backend API for the MyProject.nexa enterprise application, built with Spring Boot 3.x and Java 17.

## Tech Stack

- **Spring Boot 3.x**: Framework for building production-ready applications
- **Java 17**: Programming language
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Database access layer
- **PostgreSQL**: Relational database
- **JWT**: Token-based authentication
- **Lombok**: Code generation for boilerplate code
- **Hibernate**: ORM framework
- **Flyway**: Database migration tool
- **Springdoc OpenAPI**: API documentation (Swagger)
- **BCrypt**: Password hashing

## Architecture

The application follows a layered architecture with the following components:

```
src/
├── main/
│   ├── java/com/myproject/nexa/
│   │   ├── NexaApplication.java          # Main application class
│   │   ├── config/                       # Configuration classes
│   │   │   ├── SecurityConfig.java       # Security configuration
│   │   │   ├── CorsConfig.java           # CORS configuration
│   │   │   ├── SwaggerConfig.java        # API documentation configuration
│   │   │   └── DatabaseConfig.java       # Database configuration
│   │   ├── controllers/                  # REST controllers
│   │   │   ├── AuthController.java       # Authentication endpoints
│   │   │   ├── UserController.java       # User management endpoints
│   │   │   └── DashboardController.java  # Dashboard endpoints
│   │   ├── services/                     # Business logic interfaces
│   │   │   ├── AuthService.java          # Authentication service interface
│   │   │   ├── UserService.java          # User service interface
│   │   │   └── TokenService.java         # Token service interface
│   │   ├── impl/                         # Service implementations
│   │   │   ├── AuthServiceImpl.java      # Authentication service implementation
│   │   │   ├── UserServiceImpl.java      # User service implementation
│   │   │   └── TokenServiceImpl.java     # Token service implementation
│   │   ├── repositories/                 # Data access layer
│   │   │   ├── UserRepository.java       # User data access
│   │   │   └── RefreshTokenRepository.java # Refresh token data access
│   │   ├── entities/                     # JPA entities
│   │   │   ├── User.java                 # User entity
│   │   │   ├── Role.java                 # Role entity
│   │   │   ├── RefreshToken.java         # Refresh token entity
│   │   │   └── BaseEntity.java           # Base entity with audit fields
│   │   ├── security/                     # Security components
│   │   │   ├── JwtTokenProvider.java     # JWT token provider
│   │   │   ├── JwtAuthenticationFilter.java # JWT authentication filter
│   │   │   ├── JwtAuthenticationEntryPoint.java # JWT entry point
│   │   │   └── CustomUserDetailsService.java # User details service
│   │   ├── dto/                          # Data transfer objects
│   │   │   ├── request/                  # Request DTOs
│   │   │   │   ├── LoginRequest.java     # Login request
│   │   │   │   ├── RegisterRequest.java  # Registration request
│   │   │   │   └── RefreshTokenRequest.java # Refresh token request
│   │   │   └── response/                 # Response DTOs
│   │   │       ├── AuthResponse.java     # Authentication response
│   │   │       ├── UserResponse.java     # User response
│   │   │       └── ApiResponse.java      # Generic API response
│   │   ├── exceptions/                   # Custom exceptions
│   │   │   ├── GlobalExceptionHandler.java # Global exception handler
│   │   │   ├── ResourceNotFoundException.java # Resource not found
│   │   │   ├── BadRequestException.java  # Bad request
│   │   │   └── UnauthorizedException.java # Unauthorized
│   │   └── utils/                        # Utility classes
│   │       ├── ResponseUtil.java         # Response utilities
│   │       ├── ValidationUtil.java       # Validation utilities
│   │       └── DateUtil.java             # Date utilities
│   └── resources/
│       ├── application.yml              # Main configuration
│       ├── application-dev.yml          # Development configuration
│       ├── application-prod.yml         # Production configuration
│       └── db/migration/                # Database migration scripts
│           └── V1__init_schema.sql      # Initial schema
```

## Configuration

### Environment Variables

The application uses the following configuration properties (defined in application.yml):

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/myproject_nexa
    username: postgres
    password: password

jwt:
  secret: your-super-secret-key-change-in-production
  expiration: 86400000  # 24 hours in milliseconds
  refresh-token-expiration: 604800000  # 7 days in milliseconds
```

## Database Setup

1. Ensure PostgreSQL 15+ is installed and running
2. Update the database credentials in `application.yml`
3. The application uses Flyway for database migrations, which will automatically run the migration scripts on startup

## API Endpoints

### Authentication
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/register` - User registration
- `POST /api/v1/auth/refresh` - Refresh access token
- `POST /api/v1/auth/logout` - User logout

### Users
- `GET /api/v1/users` - Get all users (paginated)
- `GET /api/v1/users/{id}` - Get user by ID
- `GET /api/v1/users/me` - Get current user
- `PUT /api/v1/users/{id}` - Update user
- `DELETE /api/v1/users/{id}` - Delete user

### Dashboard
- `GET /api/v1/dashboard/stats` - Get dashboard statistics
- `GET /api/v1/dashboard/activity` - Get recent activity

### API Documentation
- `GET /swagger-ui.html` - Swagger UI
- `GET /v3/api-docs` - OpenAPI specification

## Running the Application

### Prerequisites
- Java 17+
- Maven 3.6+
- PostgreSQL 15+

### Development
```bash
# Build the application
mvn clean install

# Run the application
mvn spring-boot:run

# The application will be available at http://localhost:8080
```

### Production
```bash
# Package the application
mvn clean package -DskipTests

# Run the packaged JAR
java -jar target/myproject-nexa-backend-0.0.1-SNAPSHOT.jar
```

## Security

The application implements JWT-based authentication with the following features:
- Secure password hashing with BCrypt
- Access tokens with configurable expiration
- Refresh tokens for secure token refresh
- Role-based access control
- Automatic token refresh on expiration

## Database Migrations

Database schema changes are handled through Flyway migration scripts located in `src/main/resources/db/migration/`. Each migration script follows the naming convention `V{n}__{description}.sql`.

Example migration script:
```sql
-- V2__add_user_profile_fields.sql
ALTER TABLE users 
ADD COLUMN date_of_birth DATE,
ADD COLUMN gender VARCHAR(20),
ADD COLUMN profile_picture_url VARCHAR(500);
```