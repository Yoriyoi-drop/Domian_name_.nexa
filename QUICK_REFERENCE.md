# 🚀 Quick Reference - MyProject.nexa

## 📦 Quick Start

### Development Setup (One Command)
```bash
./setup.sh
```

### Manual Setup
```bash
# 1. Copy environment file
cp .env.development.template .env

# 2. Start services
docker-compose up -d

# 3. Check status
docker-compose ps
```

---

## 🔗 Access URLs

| Service | URL | Credentials |
|---------|-----|-------------|
| **Frontend** | http://localhost:3000 | - |
| **Backend API** | http://localhost:8080/api/v1 | - |
| **API Docs** | http://localhost:8080/api/v1/swagger-ui.html | - |
| **PGAdmin** | http://localhost:5050 | admin@myproject.nexa / admin123 |

---

## 🛠️ Common Commands

### Docker Operations
```bash
# Start all services
docker-compose up -d

# Stop all services
docker-compose down

# Restart specific service
docker-compose restart backend

# View logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f backend

# Rebuild and restart
docker-compose up -d --build

# Remove everything (including volumes)
docker-compose down -v
```

### Database Operations
```bash
# Create backup
./backup-db.sh backup

# List backups
./backup-db.sh list

# Restore from backup
./backup-db.sh restore /path/to/backup.sql.gz

# Access PostgreSQL CLI
docker-compose exec postgres psql -U postgres myproject_nexa

# Run SQL file
cat script.sql | docker-compose exec -T postgres psql -U postgres myproject_nexa
```

### Deployment
```bash
# Deploy to environment
./deploy.sh deploy

# Run health checks
./deploy.sh health-check

# Rollback to previous version
./deploy.sh rollback
```

### Build Commands
```bash
# Backend (Maven)
cd backend
mvn clean package -DskipTests

# Frontend (npm)
cd frontend
npm run build

# Docker build
docker-compose build
```

---

## 📁 Important Files

| File | Purpose |
|------|---------|
| `.env` | Environment configuration (DO NOT COMMIT) |
| `docker-compose.yml` | Service orchestration |
| `DEPLOYMENT_GUIDE.md` | Complete deployment guide |
| `BUILD_STATUS.md` | Build status and fixes |
| `TAHAP_2_IMPLEMENTATION_PLAN.md` | Security enhancement plan |
| `TAHAP_2_DEPLOYMENT_SUMMARY.md` | Complete summary |

---

## 🔧 Troubleshooting

### Services won't start
```bash
# Check logs
docker-compose logs -f

# Check disk space
df -h

# Restart Docker
sudo systemctl restart docker
docker-compose up -d
```

### Database connection error
```bash
# Check PostgreSQL status
docker-compose exec postgres pg_isready -U postgres

# Restart database
docker-compose restart postgres

# Reset database (WARNING: deletes data)
docker-compose down -v
docker-compose up -d
```

### Port already in use
```bash
# Find process using port
sudo lsof -i :8080

# Kill process
sudo kill -9 <PID>

# Or change port in .env
BACKEND_PORT=8081
```

### Out of memory
```bash
# Check Docker stats
docker stats

# Restart specific service
docker-compose restart backend

# Increase Docker memory limit
# Docker Desktop: Settings > Resources > Memory
```

---

## 🔐 Security Checklist

### Development
- [ ] Using `.env.development.template`
- [ ] Default passwords (OK for dev)
- [ ] HTTP is fine

### Staging
- [ ] Using `.env.staging.template`
- [ ] Changed all default passwords
- [ ] HTTPS configured
- [ ] Firewall configured

### Production
- [ ] Using `.env.production.template`
- [ ] All secrets changed to secure values
- [ ] HTTPS with valid certificate
- [ ] Firewall configured
- [ ] Backups automated
- [ ] Monitoring setup
- [ ] DNS configured

---

## 📊 Health Check Endpoints

```bash
# Backend health
curl http://localhost:8080/api/v1/actuator/health

# Database health
docker-compose exec postgres pg_isready -U postgres

# Redis health
docker-compose exec redis redis-cli ping

# All services
docker-compose ps
```

---

## 🎯 Environment Variables

### Required
- `POSTGRES_PASSWORD` - Database password
- `JWT_SECRET` - JWT signing key
- `REDIS_PASSWORD` - Redis password

### Optional
- `BACKEND_PORT` - Backend port (default: 8080)
- `FRONTEND_PORT` - Frontend port (default: 3000)
- `RATE_LIMIT_ENABLED` - Enable rate limiting (default: true)
- `CACHE_ENABLED` - Enable caching (default: true)

---

## 📞 Getting Help

1. **Check Documentation**
   - `DEPLOYMENT_GUIDE.md` - Deployment instructions
   - `README.md` - Project overview
   - `BUILD_STATUS.md` - Build information

2. **Check Logs**
   ```bash
   docker-compose logs -f
   ```

3. **Health Checks**
   ```bash
   ./deploy.sh health-check
   ```

4. **Common Issues**
   - See DEPLOYMENT_GUIDE.md > Troubleshooting section

---

## 🚦 Status Indicators

### Service Status
```bash
docker-compose ps
```

**Healthy**: State = Up, Status = healthy  
**Unhealthy**: State = Up, Status = unhealthy  
**Down**: State = Exit

### Log Levels
- **ERROR**: Immediate attention required
- **WARN**: Should be investigated
- **INFO**: Normal operation
- **DEBUG**: Detailed information (dev only)

---

## 📝 Quick Tips

1. **Always backup before major changes**
   ```bash
   ./backup-db.sh backup
   ```

2. **Check logs when something fails**
   ```bash
   docker-compose logs -f [service-name]
   ```

3. **Use health checks to verify deployment**
   ```bash
   ./deploy.sh health-check
   ```

4. **Never commit .env files**
   ```bash
   # Already in .gitignore
   ```

5. **Update dependencies regularly**
   ```bash
   # Backend
   mvn versions:display-dependency-updates
   
   # Frontend
   npm outdated
   ```

---

## 🎓 Learning Path

1. **Week 1**: Setup development environment
2. **Week 2**: Understand architecture and code structure
3. **Week 3**: Make small changes and test
4. **Week 4**: Deploy to staging
5. **Month 2**: Implement Tahap 2 security features
6. **Month 3**: Production deployment

---

**Last Updated**: 29 November 2025  
**Version**: 1.0.0  
**Status**: ✅ Ready for Deployment
