# MyProject.nexa Developer Handbook

## Table of Contents
1. [Introduction](#introduction)
2. [Project Overview](#project-overview)
3. [Getting Started](#getting-started)
4. [Project Structure](#project-structure)
5. [Development Workflow](#development-workflow)
6. [Testing](#testing)
7. [Deployment](#deployment)
8. [Troubleshooting](#troubleshooting)
9. [Security Guidelines](#security-guidelines)
10. [Performance Guidelines](#performance-guidelines)

## Introduction

Welcome to the MyProject.nexa Developer Handbook! This document is designed to help you understand our codebase, development processes, and best practices. Please read this handbook thoroughly before starting your work on the project.

### Purpose
This handbook serves as a comprehensive guide for developers working on MyProject.nexa, covering everything from initial setup to advanced development practices.

### How to Use This Handbook
- New developers should read this handbook completely
- Experienced developers can use this as a reference
- Updates to this handbook should be contributed via pull requests

## Project Overview

MyProject.nexa is an enterprise full-stack application built using modern technologies. The system follows a microservices architecture with a focus on scalability, security, and maintainability.

### Technology Stack
- **Backend**: Java 21, Spring Boot 3.2, Spring Security, Spring Data JPA
- **Frontend**: React 18, React Router, Redux Toolkit, Tailwind CSS
- **Database**: PostgreSQL with Redis for caching
- **Message Queue**: RabbitMQ
- **Infrastructure**: Docker, Kubernetes, Terraform
- **Testing**: JUnit 5, Mockito, Jest, React Testing Library

### Architecture
The application follows a layered architecture with clear separation of concerns:
- **Presentation Layer**: React-based frontend
- **API Layer**: REST APIs with Spring Boot
- **Service Layer**: Business logic implementation
- **Data Layer**: JPA repositories and database access

## Getting Started

### Prerequisites
- Java 21 (OpenJDK)
- Node.js 18+ and npm
- Docker and Docker Compose
- Git
- Maven 3.8+

### Initial Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-org/myproject-nexa.git
   cd myproject-nexa
   ```

2. **Setup backend environment**
   ```bash
   cd backend
   # Copy the development environment file
   cp .env.example .env
   # Update the environment variables as needed
   ```

3. **Setup frontend environment**
   ```bash
   cd ../frontend
   # Copy the development environment file
   cp .env.example .env
   # Update the environment variables as needed
   ```

4. **Run with Docker Compose (recommended for development)**
   ```bash
   cd ..
   docker-compose up -d
   ```

5. **Or run services separately**
   ```bash
   # Terminal 1 - Start database and other services
   docker-compose up postgres redis rabbitmq -d

   # Terminal 2 - Start backend
   cd backend
   ./mvnw spring-boot:run

   # Terminal 3 - Start frontend
   cd frontend
   npm install
   npm run dev
   ```

### Development Environment
- Backend runs on `http://localhost:8080`
- Frontend runs on `http://localhost:3000`
- Swagger UI available at `http://localhost:8080/swagger-ui.html`

## Project Structure

```
myproject-nexa/
├── backend/                 # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/myproject/nexa/
│   │   │   │   ├── annotation/      # Custom annotations
│   │   │   │   ├── aspect/          # AOP aspects
│   │   │   │   ├── config/          # Configuration classes
│   │   │   │   ├── controllers/     # REST controllers
│   │   │   │   ├── cqrs/            # CQRS implementation
│   │   │   │   ├── dto/             # Data transfer objects
│   │   │   │   ├── entities/        # JPA entities
│   │   │   │   ├── exceptions/      # Custom exceptions
│   │   │   │   ├── mapper/          # Object mappers
│   │   │   │   ├── repositories/    # JPA repositories
│   │   │   │   ├── security/        # Security configuration
│   │   │   │   ├── services/        # Service implementations
│   │   │   │   ├── utils/           # Utility classes
│   │   │   │   ├── validation/      # Validation classes
│   │   │   │   └── NexaApplication.java
│   │   └── test/                    # Test files
│   ├── pom.xml
│   └── Dockerfile.backend
├── frontend/                # React application
│   ├── public/
│   ├── src/
│   │   ├── app/             # App configuration and routing
│   │   ├── assets/          # Static assets
│   │   ├── components/      # Reusable UI components
│   │   ├── core/            # Core application logic
│   │   ├── features/        # Feature modules
│   │   ├── shared/          # Shared utilities and components
│   │   ├── styles/          # Global styles
│   │   └── utils/           # Utility functions
│   ├── package.json
│   └── Dockerfile.frontend
├── docs/                    # Documentation
├── terraform/               # Infrastructure as Code
├── kubernetes/              # Kubernetes manifests
├── .github/                 # GitHub configurations
├── docker-compose.yml
└── README.md
```

## Development Workflow

### Git Workflow
We follow Git Flow with the following main branches:

- `main`: Production-ready code
- `develop`: Integration branch for features
- `feature/*`: Feature development
- `release/*`: Release preparation
- `hotfix/*`: Production fixes

### Branch Naming Convention
- Feature: `feature/short-description`
- Bug fix: `fix/short-description`
- Enhancement: `enhancement/short-description`

### Commit Message Format
Use the conventional commits format:
```
<type>(<scope>): <short summary>
<BLANK LINE>
<body - optional>
<BLANK LINE>
<footer - optional>
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

Example:
```
feat(auth): add user registration functionality

- Implement registration API endpoint
- Add validation for user input
- Send welcome email after registration

Closes #123
```

### Pull Request Process
1. Create a feature branch from `develop`
2. Implement your changes with proper tests
3. Run all tests locally to ensure they pass
4. Submit a pull request to `develop` branch
5. Ensure all CI checks pass
6. Assign reviewers (at least 1 for features, 2 for main branch)
7. Address feedback and make requested changes
8. Get approval from maintainers
9. Merge PR after approval

## Testing

### Backend Testing
- Unit tests: Test individual methods and classes
- Integration tests: Test components working together
- Contract tests: Test API contracts with Spring Cloud Contract
- Security tests: Test security configurations

### Frontend Testing
- Unit tests: Test individual components and functions
- Integration tests: Test component interactions
- E2E tests: Test user workflows with Playwright
- Snapshot tests: Test component rendering

### Running Tests

**Backend:**
```bash
# Run all tests
./mvnw test

# Run tests with coverage
./mvnw test jacoco:report

# Run specific test class
./mvnw -Dtest=UserServiceTest test
```

**Frontend:**
```bash
# Run all tests
npm test

# Run tests with coverage
npm run test:coverage

# Run tests in watch mode
npm run test:watch
```

### Test Coverage Requirements
- Overall coverage: ≥80%
- New features: 100% coverage of new code
- Critical paths: 100% coverage

## Deployment

### Environment Configuration
We maintain separate environments:
- **Development**: Local development
- **Staging**: Pre-production testing
- **Production**: Live application

### Deployment Process
1. Code is merged to `develop` after PR approval
2. Staging deployment happens automatically via CI/CD
3. After testing in staging, merge to `main` for production
4. Production deployment happens automatically via CI/CD

### Database Migrations
- Use Flyway for database migrations
- Place migration files in `src/main/resources/db/migration`
- Follow naming convention: `V[version]__[description].sql`
- Always test migrations in staging before production

## Troubleshooting

### Common Issues

**Backend:**
- Port already in use: Check if another instance is running
- Database connection failure: Verify database is running and credentials
- Dependency conflicts: Update dependencies or use dependency management

**Frontend:**
- Module not found: Ensure package is installed and import path is correct
- CORS errors: Check backend CORS configuration
- Build errors: Verify dependencies and configuration

### Helpful Commands

**Docker:**
```bash
# View running containers
docker ps

# View container logs
docker logs <container_name>

# Execute command in container
docker exec -it <container_name> sh
```

**Database:**
```bash
# Connect to PostgreSQL
psql -h localhost -U postgres -d myproject_nexa
```

### Getting Help
- Check GitHub issues for similar problems
- Ask questions in the team Slack channel
- Create a GitHub issue if it's a bug
- Pair with team members for complex issues

## Security Guidelines

### Input Validation
- Always validate and sanitize user inputs
- Use parameterized queries to prevent SQL injection
- Apply proper authentication and authorization checks

### Password Security
- Use BCrypt with strength 12 for password encoding
- Implement strong password policies
- Never log sensitive information

### API Security
- Use JWT tokens for authentication
- Implement rate limiting to prevent abuse
- Apply proper CORS policies

## Performance Guidelines

### Backend Performance
- Use pagination for large datasets
- Implement caching for frequently accessed data
- Optimize database queries and use proper indexing
- Use async processing for long-running operations

### Frontend Performance
- Implement code splitting and lazy loading
- Optimize bundle size
- Use memoization where appropriate
- Minimize re-renders

### Monitoring
- Add custom metrics for business operations
- Implement proper logging for debugging
- Set up alerts for critical issues