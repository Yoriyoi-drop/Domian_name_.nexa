# Panduan Migrasi Domain - MyProject.nexa

## 📋 Executive Summary

Dokumen ini memberikan panduan lengkap untuk migrasi domain dari `domain.nexa` ke domain alternatif, atau strategi mempertahankan domain saat ini dengan optimasi.

---

## 🎯 Keputusan Strategis

### Opsi A: Tetap dengan domain.nexa + Proteksi Brand
**Investasi**: $87-$127/tahun  
**Timeline**: 1-2 minggu  
**Risiko**: Rendah  

### Opsi B: Migrasi ke myproject.com
**Investasi**: $12-$15/tahun  
**Timeline**: 2-4 minggu  
**Risiko**: Sedang (SEO impact)  

### Opsi C: Dual Domain Strategy
**Investasi**: $87-$115/tahun  
**Timeline**: 2-3 minggu  
**Risiko**: Rendah  

---

## 📊 Analisis Mendalam untuk MyProject.nexa

### Profil Aplikasi Anda
Berdasarkan analisis kode dan dokumentasi:

```
Tipe: Enterprise Fullstack Application
Stack: React + Vite + Spring Boot + PostgreSQL
Target: B2B/Enterprise
Fitur: Authentication, User Management, Dashboard
Deployment: Docker + Nginx + SSL
```

### Rekomendasi Berdasarkan Profil

#### ✅ REKOMENDASI #1: Dual Domain Strategy (TERBAIK)

**Setup:**
```
Primary Domain: domain.nexa (untuk branding & marketing)
Secondary Domain: myproject.com (untuk aplikasi & kredibilitas)
```

**Implementasi:**
1. **Beli myproject.com** ($12/tahun)
2. **Setup DNS:**
   - `domain.nexa` → Marketing website
   - `app.myproject.com` → Aplikasi utama
   - `api.myproject.com` → Backend API
   - `docs.myproject.com` → Dokumentasi

3. **Konfigurasi Nginx:**
```nginx
# domain.nexa - Marketing Site
server {
    listen 443 ssl http2;
    server_name domain.nexa www.domain.nexa;
    
    ssl_certificate /etc/letsencrypt/live/domain.nexa/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/domain.nexa/privkey.pem;
    
    root /var/www/marketing;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
}

# myproject.com - Main Application
server {
    listen 443 ssl http2;
    server_name app.myproject.com;
    
    ssl_certificate /etc/letsencrypt/live/myproject.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/myproject.com/privkey.pem;
    
    location / {
        proxy_pass http://frontend:3000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}

# API Endpoint
server {
    listen 443 ssl http2;
    server_name api.myproject.com;
    
    ssl_certificate /etc/letsencrypt/live/myproject.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/myproject.com/privkey.pem;
    
    location / {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

**Keuntungan:**
- ✅ Branding unik dengan domain.nexa
- ✅ Kredibilitas dengan myproject.com
- ✅ Fleksibilitas untuk berbagai use case
- ✅ SEO optimization untuk kedua domain
- ✅ Risk mitigation

**Biaya 5 Tahun:**
- domain.nexa: $375
- myproject.com: $60
- **Total: $435** ($87/tahun)

---

#### ✅ REKOMENDASI #2: Migrasi Penuh ke myproject.com

**Jika Anda Memilih Migrasi Penuh:**

### Langkah 1: Persiapan (Week 1)

1. **Beli Domain Baru**
   ```bash
   # Registrar yang direkomendasikan:
   # - Namecheap (murah, reliable)
   # - Cloudflare (gratis WHOIS privacy)
   # - Google Domains (integrasi bagus)
   ```

2. **Backup Semua Data**
   ```bash
   # Backup database
   ./backup-db.sh
   
   # Backup konfigurasi
   tar -czf config-backup.tar.gz nginx/ ssl/ .env*
   
   # Backup aplikasi
   docker save myproject-frontend:latest > frontend-backup.tar
   docker save myproject-backend:latest > backend-backup.tar
   ```

3. **Setup DNS untuk Domain Baru**
   ```
   A Record:
   myproject.com → [Server IP]
   www.myproject.com → [Server IP]
   app.myproject.com → [Server IP]
   api.myproject.com → [Server IP]
   
   CNAME Records:
   www → myproject.com
   ```

### Langkah 2: Konfigurasi (Week 2)

1. **Update Environment Variables**
   ```bash
   # frontend/.env.production
   VITE_API_BASE_URL=https://api.myproject.com
   VITE_APP_NAME=MyProject
   VITE_ENVIRONMENT=production
   ```

2. **Update Backend Configuration**
   ```yaml
   # backend/src/main/resources/application-production.yml
   server:
     domain: myproject.com
   
   cors:
     allowed-origins:
       - https://myproject.com
       - https://www.myproject.com
       - https://app.myproject.com
   
   jwt:
     issuer: myproject.com
   ```

3. **Generate SSL Certificates**
   ```bash
   # Install certbot
   sudo apt install certbot python3-certbot-nginx
   
   # Generate certificates
   sudo certbot --nginx \
     -d myproject.com \
     -d www.myproject.com \
     -d app.myproject.com \
     -d api.myproject.com \
     --email your-email@example.com \
     --agree-tos \
     --non-interactive
   ```

### Langkah 3: Testing (Week 2-3)

1. **Setup Staging Environment**
   ```bash
   # Update docker-compose.yml untuk staging
   cp docker-compose.yml docker-compose.staging.yml
   
   # Edit staging configuration
   # Gunakan subdomain: staging.myproject.com
   ```

2. **Run Tests**
   ```bash
   # Frontend tests
   cd frontend
   npm run test
   npm run build
   
   # Backend tests
   cd backend
   mvn test
   mvn verify
   
   # Integration tests
   ./run-integration-tests.sh
   ```

3. **Performance Testing**
   ```bash
   # Load testing dengan Apache Bench
   ab -n 1000 -c 10 https://staging.myproject.com/
   
   # Security scan
   ./security-scan.sh staging.myproject.com
   ```

### Langkah 4: Migration (Week 3-4)

1. **Setup 301 Redirects dari domain.nexa**
   ```nginx
   # Redirect semua traffic dari domain.nexa ke myproject.com
   server {
       listen 443 ssl http2;
       server_name domain.nexa www.domain.nexa;
       
       ssl_certificate /etc/letsencrypt/live/domain.nexa/fullchain.pem;
       ssl_certificate_key /etc/letsencrypt/live/domain.nexa/privkey.pem;
       
       return 301 https://myproject.com$request_uri;
   }
   ```

2. **Deploy ke Production**
   ```bash
   # Deploy dengan script
   ./deploy.sh deploy
   
   # Atau manual
   docker-compose -f docker-compose.yml up -d --build
   ```

3. **Monitor & Verify**
   ```bash
   # Check health
   curl https://myproject.com/health
   curl https://api.myproject.com/actuator/health
   
   # Monitor logs
   docker-compose logs -f
   
   # Check SSL
   openssl s_client -connect myproject.com:443 -servername myproject.com
   ```

### Langkah 5: Post-Migration (Week 4+)

1. **Update Dokumentasi**
   - [ ] README.md
   - [ ] API Documentation
   - [ ] Deployment guides
   - [ ] User documentation

2. **Notify Users**
   ```
   Subject: Important: Domain Migration Notice
   
   Dear Users,
   
   We're excited to announce that MyProject has migrated to a new domain:
   
   Old: https://domain.nexa
   New: https://myproject.com
   
   All your data and settings have been preserved. The old domain will
   redirect to the new one for the next 6 months.
   
   Please update your bookmarks.
   
   Best regards,
   MyProject Team
   ```

3. **SEO Maintenance**
   - [ ] Submit new sitemap to Google Search Console
   - [ ] Update Google Analytics
   - [ ] Update social media links
   - [ ] Update email signatures
   - [ ] Update business cards

4. **Monitor Metrics**
   ```bash
   # Setup monitoring
   # - Google Analytics
   # - Uptime monitoring (UptimeRobot)
   # - Error tracking (Sentry)
   # - Performance monitoring (New Relic)
   ```

---

## 🔧 Konfigurasi Teknis Detail

### Docker Compose Update

```yaml
# docker-compose.yml
version: '3.8'

services:
  frontend:
    build:
      context: ./frontend
      dockerfile: ../Dockerfile.frontend
    environment:
      - VITE_API_BASE_URL=https://api.myproject.com
      - VITE_APP_NAME=MyProject
    networks:
      - app-network

  backend:
    build:
      context: ./backend
      dockerfile: ../Dockerfile.backend
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - SERVER_DOMAIN=myproject.com
      - CORS_ALLOWED_ORIGINS=https://myproject.com,https://www.myproject.com,https://app.myproject.com
    networks:
      - app-network

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./ssl:/etc/letsencrypt:ro
    depends_on:
      - frontend
      - backend
    networks:
      - app-network

  postgres:
    image: postgres:15-alpine
    environment:
      - POSTGRES_DB=myproject
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=${DB_PASSWORD}
    volumes:
      - postgres-data:/var/lib/postgresql/data
    networks:
      - app-network

networks:
  app-network:
    driver: bridge

volumes:
  postgres-data:
```

### Nginx Configuration Complete

```nginx
# nginx/nginx.conf
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log warn;
pid /var/run/nginx.pid;

events {
    worker_connections 1024;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for"';

    access_log /var/log/nginx/access.log main;

    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 65;
    types_hash_max_size 2048;

    # SSL Settings
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_prefer_server_ciphers on;
    ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;

    # Gzip Settings
    gzip on;
    gzip_vary on;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_types text/plain text/css text/xml text/javascript 
               application/json application/javascript application/xml+rss;

    # Rate Limiting
    limit_req_zone $binary_remote_addr zone=api_limit:10m rate=10r/s;
    limit_req_zone $binary_remote_addr zone=login_limit:10m rate=5r/m;

    # Redirect HTTP to HTTPS
    server {
        listen 80;
        server_name myproject.com www.myproject.com app.myproject.com api.myproject.com;
        return 301 https://$server_name$request_uri;
    }

    # Main Website
    server {
        listen 443 ssl http2;
        server_name myproject.com www.myproject.com;

        ssl_certificate /etc/letsencrypt/live/myproject.com/fullchain.pem;
        ssl_certificate_key /etc/letsencrypt/live/myproject.com/privkey.pem;

        # Security Headers
        add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
        add_header X-Frame-Options "SAMEORIGIN" always;
        add_header X-Content-Type-Options "nosniff" always;
        add_header X-XSS-Protection "1; mode=block" always;
        add_header Referrer-Policy "strict-origin-when-cross-origin" always;

        location / {
            proxy_pass http://frontend:3000;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection 'upgrade';
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_cache_bypass $http_upgrade;
        }

        location /health {
            access_log off;
            return 200 "healthy\n";
            add_header Content-Type text/plain;
        }
    }

    # API Server
    server {
        listen 443 ssl http2;
        server_name api.myproject.com;

        ssl_certificate /etc/letsencrypt/live/myproject.com/fullchain.pem;
        ssl_certificate_key /etc/letsencrypt/live/myproject.com/privkey.pem;

        # Security Headers
        add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
        add_header X-Content-Type-Options "nosniff" always;

        # Rate limiting for API
        location /api/ {
            limit_req zone=api_limit burst=20 nodelay;
            
            proxy_pass http://backend:8080;
            proxy_http_version 1.1;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            
            # CORS headers
            add_header Access-Control-Allow-Origin "https://myproject.com" always;
            add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS" always;
            add_header Access-Control-Allow-Headers "Authorization, Content-Type" always;
            
            if ($request_method = 'OPTIONS') {
                return 204;
            }
        }

        # Stricter rate limiting for login
        location /api/v1/auth/login {
            limit_req zone=login_limit burst=3 nodelay;
            
            proxy_pass http://backend:8080;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # Health check endpoint
        location /actuator/health {
            proxy_pass http://backend:8080;
            access_log off;
        }
    }

    # Old domain redirect (domain.nexa)
    server {
        listen 443 ssl http2;
        server_name domain.nexa www.domain.nexa;

        ssl_certificate /etc/letsencrypt/live/domain.nexa/fullchain.pem;
        ssl_certificate_key /etc/letsencrypt/live/domain.nexa/privkey.pem;

        return 301 https://myproject.com$request_uri;
    }
}
```

---

## 📊 Checklist Migrasi

### Pre-Migration
- [ ] Beli domain baru
- [ ] Setup DNS records
- [ ] Backup semua data
- [ ] Backup konfigurasi
- [ ] Test di staging environment
- [ ] Siapkan rollback plan

### Migration Day
- [ ] Generate SSL certificates
- [ ] Update environment variables
- [ ] Update konfigurasi backend
- [ ] Update konfigurasi frontend
- [ ] Deploy ke production
- [ ] Setup 301 redirects
- [ ] Verify SSL certificates
- [ ] Test semua endpoints
- [ ] Monitor error logs

### Post-Migration
- [ ] Update dokumentasi
- [ ] Notify users via email
- [ ] Update social media
- [ ] Submit new sitemap
- [ ] Update Google Analytics
- [ ] Monitor traffic & errors
- [ ] Check SEO rankings
- [ ] Verify email deliverability
- [ ] Update marketing materials
- [ ] Monitor for 30 days

---

## 🚨 Rollback Plan

Jika terjadi masalah serius:

```bash
# 1. Restore DNS ke domain.nexa
# Update DNS A records kembali ke konfigurasi lama

# 2. Restore konfigurasi lama
tar -xzf config-backup.tar.gz

# 3. Restore Docker images
docker load < frontend-backup.tar
docker load < backend-backup.tar

# 4. Restart services
docker-compose down
docker-compose up -d

# 5. Restore database (jika perlu)
./restore-db.sh backup-file.sql

# 6. Verify
curl https://domain.nexa/health
```

---

## 💰 Analisis ROI

### Skenario 1: Tetap dengan domain.nexa
```
Biaya 5 tahun: $375
Benefit: Brand uniqueness
Risk: Lower trust, higher cost
ROI: Moderate (jika branding berhasil)
```

### Skenario 2: Migrasi ke myproject.com
```
Biaya 5 tahun: $60
Benefit: Higher trust, better SEO
Risk: Migration downtime, SEO impact
ROI: High (cost savings + credibility)
```

### Skenario 3: Dual Domain
```
Biaya 5 tahun: $435
Benefit: Best of both worlds
Risk: Complexity in management
ROI: Very High (flexibility + protection)
```

---

## 📞 Support & Resources

### Jika Butuh Bantuan
1. **DNS Issues**: Cloudflare Community
2. **SSL Issues**: Let's Encrypt Community
3. **Nginx Config**: Nginx Forums
4. **Docker**: Docker Community Slack

### Monitoring Tools
- **Uptime**: UptimeRobot (free)
- **SSL Monitoring**: SSL Labs
- **Performance**: Google PageSpeed Insights
- **SEO**: Google Search Console

---

## 🎯 Rekomendasi Final

Berdasarkan analisis mendalam terhadap MyProject.nexa:

### ⭐ REKOMENDASI UTAMA: Dual Domain Strategy

**Alasan:**
1. ✅ Mempertahankan brand identity unik (domain.nexa)
2. ✅ Mendapatkan kredibilitas enterprise (myproject.com)
3. ✅ Fleksibilitas untuk berbagai use case
4. ✅ Risk mitigation jika ada masalah dengan salah satu domain
5. ✅ SEO optimization untuk kedua domain

**Implementasi:**
- Marketing & Branding: `domain.nexa`
- Production App: `app.myproject.com`
- API: `api.myproject.com`
- Documentation: `docs.myproject.com`

**Timeline:** 2-3 minggu  
**Budget:** $87/tahun ($435 untuk 5 tahun)  
**Risk Level:** Rendah  
**Expected ROI:** Tinggi  

---

**Dibuat**: 2025-11-30  
**Versi**: 1.0  
**Status**: Ready for Implementation
