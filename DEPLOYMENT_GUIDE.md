# Deployment Guide - MyProject.nexa

## 📋 Table of Contents
1. [Prerequisites](#prerequisites)
2. [Environment Setup](#environment-setup)
3. [Local Development](#local-development)
4. [Staging Deployment](#staging-deployment)
5. [Production Deployment](#production-deployment)
6. [Monitoring & Maintenance](#monitoring--maintenance)
7. [Troubleshooting](#troubleshooting)

---

## 🔧 Prerequisites

### Required Software
- **Docker**: >= 20.10
- **Docker Compose**: >= 2.0
- **Git**: >= 2.30
- **OpenSSL**: For SSL certificate generation
- **curl/wget**: For health checks

### System Requirements

#### Development
- CPU: 2 cores
- RAM: 4GB
- Disk: 10GB

#### Staging
- CPU: 4 cores
- RAM: 8GB
- Disk: 50GB

#### Production
- CPU: 8 cores
- RAM: 16GB
- Disk: 100GB
- SSD recommended

---

## 🌍 Environment Setup

### 1. Clone Repository
```bash
git clone https://github.com/your-username/myproject.nexa.git
cd myproject.nexa
```

### 2. Choose Environment

#### Development
```bash
cp .env.development.template .env
```

#### Staging
```bash
cp .env.staging.template .env
```

#### Production
```bash
cp .env.production.template .env
```

### 3. Update Environment Variables
Edit `.env` file and update all values, especially:
- Database passwords
- JWT secret
- Redis password
- API URLs

**⚠️ IMPORTANT**: Never commit `.env` files to version control!

---

## 💻 Local Development

### Quick Start
```bash
# 1. Setup environment
cp .env.development.template .env

# 2. Build and start services
docker-compose up -d

# 3. Check status
docker-compose ps

# 4. View logs
docker-compose logs -f
```

### Access Points
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api/v1
- **API Docs**: http://localhost:8080/api/v1/swagger-ui.html
- **PGAdmin**: http://localhost:5050

### Development Commands
```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# Rebuild services
docker-compose up -d --build

# View logs
docker-compose logs -f [service-name]

# Execute command in container
docker-compose exec backend bash
docker-compose exec postgres psql -U postgres

# Clean up everything
docker-compose down -v
```

---

## 🧪 Staging Deployment

### 1. Server Setup
```bash
# SSH to staging server
ssh user@staging-server

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

### 2. Deploy Application
```bash
# Clone repository
git clone https://github.com/your-username/myproject.nexa.git
cd myproject.nexa

# Setup environment
cp .env.staging.template .env
nano .env  # Update all values

# Generate SSL certificates (if needed)
./generate-certs.sh

# Deploy
chmod +x deploy.sh
./deploy.sh deploy
```

### 3. Verify Deployment
```bash
# Check services
docker-compose ps

# Run health checks
./deploy.sh health-check

# View logs
docker-compose logs -f
```

---

## 🚀 Production Deployment

### Pre-Deployment Checklist
- [ ] All environment variables configured
- [ ] SSL certificates generated and valid
- [ ] Database backup created
- [ ] DNS records configured
- [ ] Firewall rules configured
- [ ] Monitoring setup
- [ ] Backup strategy in place

### 1. Server Preparation
```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install required packages
sudo apt install -y docker.io docker-compose git curl wget openssl

# Configure firewall
sudo ufw allow 22/tcp   # SSH
sudo ufw allow 80/tcp   # HTTP
sudo ufw allow 443/tcp  # HTTPS
sudo ufw enable
```

### 2. SSL Certificate Setup

#### Option A: Let's Encrypt (Recommended)
```bash
# Install certbot
sudo apt install -y certbot

# Generate certificate
sudo certbot certonly --standalone -d myproject.nexa -d www.myproject.nexa -d api.myproject.nexa

# Copy certificates
sudo cp /etc/letsencrypt/live/myproject.nexa/fullchain.pem ./ssl/certs/
sudo cp /etc/letsencrypt/live/myproject.nexa/privkey.pem ./ssl/private/
```

#### Option B: Self-Signed (Development/Testing)
```bash
./generate-certs.sh
```

### 3. Database Setup
```bash
# Create database backup location
sudo mkdir -p /var/backups/postgres
sudo chmod 700 /var/backups/postgres

# Setup automated backups (cron)
crontab -e
# Add: 0 2 * * * /opt/myproject-nexa/backup-db.sh
```

### 4. Deploy Application
```bash
# Clone repository
cd /opt
sudo git clone https://github.com/your-username/myproject.nexa.git
cd myproject.nexa

# Setup environment
sudo cp .env.production.template .env
sudo nano .env  # Update ALL values with secure credentials

# Build application
cd backend
mvn clean package -DskipTests
cd ../frontend
npm run build
cd ..

# Deploy with Docker
sudo docker-compose up -d --build

# Verify deployment
sudo docker-compose ps
./deploy.sh health-check
```

### 5. Post-Deployment
```bash
# Setup log rotation
sudo nano /etc/logrotate.d/myproject-nexa

# Configure monitoring
# Setup Prometheus/Grafana or your monitoring solution

# Test application
curl -I https://myproject.nexa
curl https://api.myproject.nexa/api/v1/actuator/health
```

---

## 📊 Monitoring & Maintenance

### Health Checks
```bash
# Manual health check
./deploy.sh health-check

# Backend health
curl https://api.myproject.nexa/api/v1/actuator/health

# Frontend health
curl https://myproject.nexa/health

# Database health
docker-compose exec postgres pg_isready -U postgres
```

### Logs
```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres

# Export logs
docker-compose logs > logs-$(date +%Y%m%d).txt
```

### Backup & Restore

#### Database Backup
```bash
# Manual backup
docker-compose exec postgres pg_dump -U postgres myproject_nexa > backup-$(date +%Y%m%d).sql

# Automated backup script
./backup-db.sh
```

#### Database Restore
```bash
# Restore from backup
cat backup-20251129.sql | docker-compose exec -T postgres psql -U postgres myproject_nexa
```

### Updates & Maintenance
```bash
# Pull latest code
git pull origin main

# Rebuild and restart
docker-compose up -d --build

# If issues occur, rollback
./deploy.sh rollback
```

---

## 🔍 Troubleshooting

### Common Issues

#### 1. Services Not Starting
```bash
# Check logs
docker-compose logs -f

# Check disk space
df -h

# Check memory
free -h

# Restart services
docker-compose restart
```

#### 2. Database Connection Issues
```bash
# Check PostgreSQL status
docker-compose exec postgres pg_isready -U postgres

# Check connection from backend
docker-compose exec backend ping postgres

# Reset database
docker-compose down -v
docker-compose up -d
```

#### 3. Frontend Not Loading
```bash
# Check Nginx configuration
docker-compose exec nginx nginx -t

# Reload Nginx
docker-compose exec nginx nginx -s reload

# Check frontend build
docker-compose exec frontend ls -la /usr/share/nginx/html
```

#### 4. High Memory Usage
```bash
# Check container stats
docker stats

# Restart specific service
docker-compose restart backend

# Adjust memory limits in docker-compose.yml
```

### Performance Optimization

#### Database
```bash
# Analyze database
docker-compose exec postgres psql -U postgres -c "ANALYZE;"

# Vacuum database
docker-compose exec postgres psql -U postgres -c "VACUUM FULL;"

# Check slow queries
docker-compose exec postgres psql -U postgres -c "SELECT * FROM pg_stat_statements ORDER BY total_time DESC LIMIT 10;"
```

#### Redis
```bash
# Check Redis memory
docker-compose exec redis redis-cli INFO memory

# Clear cache (if needed)
docker-compose exec redis redis-cli FLUSHDB
```

---

## 🔐 Security Best Practices

1. **Never commit `.env` files** to version control
2. **Use strong passwords** for all services
3. **Enable SSL/TLS** in production
4. **Regular security updates** for all dependencies
5. **Monitor logs** for suspicious activity
6. **Backup regularly** and test restore procedures
7. **Use secrets management** (Vault/AWS Secrets Manager) in production
8. **Implement rate limiting** to prevent abuse
9. **Enable audit logging** for compliance
10. **Regular security audits** and penetration testing

---

## 📞 Support & Contact

For issues or questions:
- **Documentation**: See `README.md` and other docs
- **Issues**: Create GitHub issue
- **Security**: security@myproject.nexa

---

## 📚 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Nginx Documentation](https://nginx.org/en/docs/)
