# Domain.nexa

This repository contains a full-stack web application project using modern technologies.

## Project Overview

The domain.nexa project is a comprehensive web application that implements enterprise-grade security, authentication, and modern UI/UX practices. The application follows clean architecture principles and includes both frontend and backend components.

## Features

- **Authentication & Authorization**: Secure user management with role-based access control
- **Rate Limiting**: Redis-backed rate limiting for API protection
- **Security**: CSRF protection, input sanitization, and secure session handling
- **Multi-theme UI**: Support for multiple color themes (Yin-Yang, Ocean, Forest, Sunset, Midnight)
- **API Integration**: RESTful API with comprehensive endpoints
- **Docker Support**: Containerized deployment with Docker Compose

## Tech Stack

### Backend
- Java 21 with Spring Boot
- Spring Security for authentication and authorization
- PostgreSQL for data persistence
- Redis for caching and rate limiting
- MapStruct for object mapping
- Lombok for boilerplate reduction

### Frontend
- React 18 with Vite
- Tailwind CSS for styling
- React Router for navigation
- Redux Toolkit for state management
- Lucide React for icons
- TanStack Query for data fetching

### Infrastructure
- Docker & Docker Compose for containerization
- Nginx as reverse proxy
- PostgreSQL database
- Redis cache

## Installation & Setup

### Prerequisites
- Docker and Docker Compose
- Node.js v18+ (for frontend development)
- Java 21 (for backend development)

### Development Setup

1. Clone the repository:
```bash
git clone https://github.com/Yoriyoi-drop/Domian_name_.nexa.git
cd Domian_name_.nexa
```

2. Set up environment variables (create .env files based on templates):
```bash
cp .env production.template .env
```

3. Start the services using Docker Compose:
```bash
docker-compose up -d
```

4. Access the application:
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080/api/v1

## Security Features

- Rate limiting with Redis
- CSRF protection
- Input sanitization using OWASP guidelines
- Secure session handling
- Password policy enforcement
- Brute force protection
- HttpOnly and Secure cookies

## Architecture

The project follows Clean Architecture principles with:
- Domain layer
- Application layer
- Infrastructure layer

## Deployment

The application is designed for containerized deployment using Docker Compose. Production deployment configurations are available for:
- Load balancing
- SSL termination
- Database backup
- Monitoring and logging

## License

This project is licensed under the MIT License - see the LICENSE file for details.