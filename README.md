# MyProject.nexa - Enterprise Fullstack Application

This is an enterprise-level fullstack application built with React + Vite + TailwindCSS + shadcn/ui for the frontend and Spring Boot 3.x for the backend, with PostgreSQL as the database. The application is deployed using Docker and Nginx with SSL support.

## 🚀 Project Overview

MyProject.nexa is a comprehensive enterprise solution that demonstrates modern fullstack development practices with:

- **Frontend**: React 18 + Vite + TailwindCSS + shadcn/ui
- **Backend**: Spring Boot 3.x with Java 17
- **Database**: PostgreSQL 15
- **Authentication**: JWT-based with refresh tokens
- **Infrastructure**: Docker, Docker Compose, Nginx, SSL
- **CI/CD**: GitHub Actions

## 📋 Table of Contents

- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Environment Variables](#environment-variables)
- [API Documentation](#api-documentation)
- [Deployment](#deployment)
- [Testing](#testing)
- [Security](#security)
- [CI/CD](#cicd)

## 🏗️ Architecture

The application follows a modular architecture with clear separation of concerns:

### Frontend Architecture
- **Feature-based structure** for better maintainability
- **Component-based UI** using shadcn/ui components
- **State management** with React Context and React Query
- **API integration** with centralized axios client and interceptors

### Backend Architecture
- **Layered architecture** (Controller, Service, Repository, Entity)
- **Security layer** with JWT authentication and authorization
- **Database layer** with JPA/Hibernate and PostgreSQL
- **Configuration management** with Spring profiles

## 🛠️ Tech Stack

### Frontend
- React 18
- Vite 4
- TailwindCSS
- shadcn/ui components
- React Router v6
- Axios for API calls
- React Query for data fetching
- Lucide React for icons

### Backend
- Spring Boot 3.x
- Java 17
- Spring Security
- Spring Data JPA
- PostgreSQL 15
- JWT for authentication
- Hibernate ORM
- Flyway for database migrations
- Lombok
- Validation
- OpenAPI (Swagger) for API documentation

### Infrastructure
- Docker & Docker Compose
- Nginx as reverse proxy
- SSL/TLS with Let's Encrypt
- GitHub Actions for CI/CD

## 📁 Project Structure

```
domain.nexa/
├── frontend/                    # React frontend application
│   ├── public/                  # Static assets
│   ├── src/
│   │   ├── app/                 # Main app configuration
│   │   ├── features/            # Feature modules (auth, dashboard, users)
│   │   ├── shared/              # Shared components, hooks, utils
│   │   ├── core/                # Core logic (API, auth, config)
│   │   ├── assets/              # Images, icons, fonts
│   │   └── styles/              # Global styles
│   ├── package.json
│   ├── vite.config.js
│   └── ...
├── backend/                     # Spring Boot backend
│   ├── src/main/java/
│   │   ├── controllers/         # REST controllers
│   │   ├── services/            # Business logic
│   │   ├── repositories/        # Data access layer
│   │   ├── entities/            # JPA entities
│   │   ├── security/            # Security components
│   │   ├── dto/                 # Data transfer objects
│   │   ├── exceptions/          # Exception handling
│   │   ├── config/              # Configuration classes
│   │   └── utils/               # Utility classes
│   ├── src/main/resources/
│   │   ├── application.yml      # Main configuration
│   │   └── db/migration/        # Database migration scripts
│   ├── pom.xml
│   └── ...
├── nginx/                       # Nginx configuration
├── ssl/                         # SSL certificates
├── .github/workflows/           # CI/CD pipelines
├── docker-compose.yml           # Docker orchestration
├── Dockerfile.frontend          # Frontend Dockerfile
├── Dockerfile.backend           # Backend Dockerfile
├── deploy.sh                    # Deployment script
└── README.md
```

## 📋 Prerequisites

### Local Development
- Node.js 18+ (for frontend)
- Java 17+ (for backend)
- Maven 3.6+ (for backend)
- Docker & Docker Compose
- PostgreSQL 15 (or Docker for automatic setup)

### Production Deployment
- Ubuntu Server 20.04+
- Docker & Docker Compose
- Domain name configured with DNS A record
- SSL certificate (Let's Encrypt recommended)

## 🚀 Getting Started

### Local Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/myproject.nexa.git
   cd myproject.nexa
   ```

2. **Set up frontend**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

3. **Set up backend**
   ```bash
   # In a new terminal
   cd backend
   mvn spring-boot:run
   ```

4. **Set up database with Docker**
   ```bash
   # In the project root
   docker-compose up -d postgres
   ```

### Using Docker Compose (Recommended)

1. **Generate SSL certificates for development**
   ```bash
   ./generate-certs.sh
   ```

2. **Start all services**
   ```bash
   docker-compose up -d
   ```

3. **Access the application**
   - Frontend: https://myproject.nexa (or https://localhost:3000)
   - API: https://api.myproject.nexa/v3/api-docs (or https://localhost:8080)
   - PGAdmin: http://localhost:5050

## 🔐 Environment Variables

### Frontend (.env.development, .env.production)

```env
VITE_API_BASE_URL=https://api.myproject.nexa
VITE_APP_NAME=MyProject.nexa
VITE_ENVIRONMENT=development
```

### Backend (application.properties or environment variables)

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/myproject_nexa
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
JWT_SECRET=your-super-secret-key-change-in-production
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
```

## 📚 API Documentation

The API is documented using OpenAPI (Swagger). After starting the backend, you can access the documentation at:
- Swagger UI: `https://api.myproject.nexa/swagger-ui.html`
- API Docs: `https://api.myproject.nexa/v3/api-docs`

### Available Endpoints

#### Authentication
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/register` - User registration
- `POST /api/v1/auth/refresh` - Refresh access token
- `POST /api/v1/auth/logout` - User logout

#### Users
- `GET /api/v1/users` - Get all users (paginated)
- `GET /api/v1/users/{id}` - Get user by ID
- `GET /api/v1/users/me` - Get current user
- `PUT /api/v1/users/{id}` - Update user
- `DELETE /api/v1/users/{id}` - Delete user

#### Dashboard
- `GET /api/v1/dashboard/stats` - Get dashboard statistics
- `GET /api/v1/dashboard/activity` - Get recent activity

## 📦 Deployment

### Production Deployment

1. **Configure domain** - Point `myproject.nexa` and `api.myproject.nexa` to your server's IP address

2. **Install prerequisites** on your Ubuntu server:
   ```bash
   # Update system
   sudo apt update && sudo apt upgrade -y
   
   # Install Docker
   curl -fsSL https://get.docker.com -o get-docker.sh
   sudo sh get-docker.sh
   sudo usermod -aG docker $USER
   
   # Install Docker Compose
   sudo apt install docker-compose-plugin
   ```

3. **Set up SSL certificates** with Let's Encrypt:
   ```bash
   sudo apt install certbot python3-certbot-nginx
   sudo certbot --nginx -d myproject.nexa -d api.myproject.nexa
   ```

4. **Deploy using the deployment script**:
   ```bash
   ./deploy.sh deploy
   ```

### Using the Deployment Script

The deployment script automates the entire process:

```bash
# Deploy the application
./deploy.sh deploy

# Rollback to previous version
./deploy.sh rollback

# Run health checks
./deploy.sh health-check
```

## 🧪 Testing

### Frontend Testing
```bash
cd frontend
npm test
npm run lint
```

### Backend Testing
```bash
cd backend
mvn test
mvn verify
```

### Health Checks
- Frontend health: `https://myproject.nexa/health`
- Backend health: `https://api.myproject.nexa/actuator/health`

## 🔒 Security

### Authentication
- JWT-based authentication with refresh tokens
- Secure password hashing with BCrypt
- Token expiration and automatic refresh
- Role-based access control

### HTTPS
- SSL/TLS encryption for all traffic
- HSTS headers for security
- Content Security Policy (CSP)

### Input Validation
- Server-side validation for all inputs
- Sanitization of user inputs
- Protection against common attacks (XSS, CSRF, SQL Injection)

## 🔄 CI/CD

The project includes a GitHub Actions CI/CD pipeline that:

1. **Tests** - Runs automated tests on pull requests and pushes
2. **Builds** - Creates Docker images for frontend and backend
3. **Deploys** - Deploys to production when changes are merged to main
4. **Notifies** - Sends notifications on deployment success/failure

### Pipeline Configuration

The CI/CD pipeline is configured in `.github/workflows/deploy.yml` and includes:

- Frontend linting and build
- Backend compilation and tests
- Docker image building and pushing
- Production deployment
- Health checks
- Notification system

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

Made with ❤️ for the open source community.

For support, please open an issue in the repository or contact the maintainers.