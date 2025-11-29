# MyProject.nexa DevOps & Infrastructure

This section contains the infrastructure and deployment configuration for the MyProject.nexa enterprise application.

## Infrastructure Overview

The application is deployed using the following infrastructure components:

- **Docker**: Containerization of all services
- **Docker Compose**: Orchestration of multi-container applications
- **Nginx**: Reverse proxy and load balancer
- **SSL/TLS**: HTTPS encryption with Let's Encrypt
- **PostgreSQL**: Primary database
- **PGAdmin**: Database management interface

## Architecture Diagram

```
Internet
    |
    v
Nginx Reverse Proxy (HTTPS)
    |
    |-------------------|-------------------|
    |                   |                   |
Frontend           Backend API        PGAdmin
(React App)       (Spring Boot)      (Database UI)
    |                   |                   |
    v                   v                   v
Static Files      PostgreSQL Database    Database
Serving          Connection Pooling     Management
```

## Container Services

### Services in docker-compose.yml:

1. **postgres**: PostgreSQL 15 database server
2. **pgadmin**: Web-based PostgreSQL administration tool
3. **backend**: Spring Boot API server
4. **frontend**: React application served by Nginx
5. **nginx**: Nginx reverse proxy for HTTPS termination

## Setup Instructions

### Prerequisites
- Docker 20.10+
- Docker Compose v2+
- OpenSSL (for certificate generation)

### Development Setup

1. **Generate SSL certificates** (for development):
   ```bash
   ./generate-certs.sh
   ```

2. **Build and start services**:
   ```bash
   docker-compose up -d
   ```

3. **Access the application**:
   - Frontend: https://myproject.nexa
   - API: https://api.myproject.nexa
   - PGAdmin: http://localhost:5050

### Production Setup

1. **Update environment variables** in `.env` file with production values:
   ```env
   POSTGRES_PASSWORD=your_secure_password
   JWT_SECRET=your_very_secure_jwt_secret
   ```

2. **Configure DNS** to point `myproject.nexa` and `api.myproject.nexa` to your server's IP address.

3. **Set up SSL with Let's Encrypt**:
   ```bash
   sudo apt install certbot python3-certbot-nginx
   sudo certbot --nginx -d myproject.nexa -d api.myproject.nexa
   ```

4. **Deploy using the deployment script**:
   ```bash
   ./deploy.sh deploy
   ```

## SSL Certificate Management

### For Development
The `generate-certs.sh` script creates self-signed certificates for local development.

### For Production
Use Let's Encrypt with Certbot for free SSL certificates:

```bash
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d myproject.nexa -d api.myproject.nexa
```

To auto-renew certificates, add a cron job:
```bash
sudo crontab -e
# Add this line to run twice daily
0 12 * * * /usr/bin/certbot renew --quiet
```

## Security Measures

1. **HTTPS**: All traffic is encrypted using TLS
2. **Reverse Proxy**: Nginx handles SSL termination and security headers
3. **Container Isolation**: Each service runs in isolated containers
4. **Network Segmentation**: Services communicate through internal network
5. **Health Checks**: Automated monitoring of service status
6. **Environment Variables**: Sensitive data stored outside the codebase

## Backup Strategy

1. **Database Backups**: Regular PostgreSQL dumps
   ```bash
   docker exec nexa-postgres pg_dump -U postgres myproject_nexa > backup.sql
   ```

2. **Configuration Backups**: Version control for all configuration files

3. **Container Images**: Store images in a container registry

## Monitoring and Logging

1. **Application Logs**: Available via Docker logs
   ```bash
   docker-compose logs -f backend
   docker-compose logs -f frontend
   ```

2. **Nginx Logs**: Access and error logs in Nginx container

3. **Health Checks**: Built-in endpoints for service monitoring

## Deployment Commands

```bash
# View service status
docker-compose ps

# View logs
docker-compose logs

# Rebuild and restart services
docker-compose up -d --build

# Stop all services
docker-compose down

# Scale services
docker-compose up -d --scale backend=2
```

## Troubleshooting

### Common Issues

1. **Port Conflicts**: Make sure ports 80, 443, 5432, 8080, 3000, 5050 are available

2. **Certificate Issues**: 
   - Ensure certificates are in the correct location
   - Check file permissions

3. **Database Connection**:
   - Verify database service is running
   - Check connection parameters in environment variables

4. **Environment Variables**: Ensure all required environment variables are set

For detailed troubleshooting, check container logs:
```bash
docker-compose logs <service-name>
```