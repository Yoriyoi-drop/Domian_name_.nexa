Overview
  Dokumen ini menjelaskan langkah-langkah untuk melakukan deployment aplikasi
  domain.nexa ke server production. Proses ini mencakup konfigurasi server, setup
  environment, dan deployment aplikasi secara aman.

  Persyaratan Sistem
   - Server Linux (Ubuntu 20.04 LTS atau lebih baru)
   - RAM: Minimal 4GB
   - Storage: Minimal 20GB
   - Hak akses root atau sudo
   - Koneksi internet stabil

  1. Setup Server Awal

  1.1. Update Sistem

   1 sudo apt update && sudo apt upgrade -y

  1.2. Install Dependencies

   1 # Install Docker dan Docker Compose
   2 curl -fsSL https://get.docker.com -o get-docker.sh
   3 sudo sh get-docker.sh
   4 sudo usermod -aG docker $USER
   5 
   6 # Install Docker Compose
   7 sudo curl -L 
     "https://github.com/docker/compose/releases/latest/download/docker-c
     ompose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
   8 sudo chmod +x /usr/local/bin/docker-compose

  1.3. Setup Firewall

   1 sudo ufw enable
   2 sudo ufw allow ssh
   3 sudo ufw allow 80
   4 sudo ufw allow 443
   5 sudo ufw allow 8080

  2. Persiapan Environment Variables

  2.1. Buat File Konfigurasi Aman

   1 mkdir -p ~/production-config
   2 cd ~/production-config

  2.2. Buat File Environment (.env)

    1 cat > .env << 'EOF'
    2 # Environment
    3 ENVIRONMENT=production
    4 SPRING_PROFILES_ACTIVE=prod
    5 
    6 # Database Configuration
    7 POSTGRES_DB=myproject_nexa_prod
    8 POSTGRES_USER=postgres
    9 POSTGRES_PASSWORD=[isi_password_yang_kuat]
   10 POSTGRES_PORT=5432
   11 
   12 # Redis Configuration
   13 REDIS_PASSWORD=[isi_redis_password]
   14 REDIS_PORT=6379
   15 
   16 # JWT Configuration
   17 JWT_SECRET=[isi_secret_key_yang_kuat]
   18 JWT_EXPIRATION=86400000
   19 JWT_REFRESH_EXPIRATION=604800000
   20 
   21 # Application Ports
   22 BACKEND_PORT=8080
   23 FRONTEND_PORT=3000
   24 HTTP_PORT=80
   25 HTTPS_PORT=443
   26 PGADMIN_PORT=5050
   27 
   28 # SSL Configuration
   29 SSL_CERT_PATH=/etc/ssl/certs/myproject.nexa.crt
   30 SSL_KEY_PATH=/etc/ssl/private/myproject.nexa.key
   31 
   32 # Feature Flags
   33 RATE_LIMIT_ENABLED=true
   34 CACHE_ENABLED=true
   35 AUDIT_ENABLED=true
   36 SECURITY_LOG_LEVEL=INFO
   37 
   38 # Rate Limiting
   39 RATE_LIMIT_WINDOW=60
   40 RATE_LIMIT_MAX_REQUESTS=1000
   41 RATE_LIMIT_PER_USER=true
   42 
   43 # Cache Configuration
   44 CACHE_TTL_SECONDS=3600
   45 CACHE_MAX_SIZE=1000
   46 
   47 # Security
   48 MAX_LOGIN_ATTEMPTS=3
   49 LOCKOUT_DURATION_MINUTES=15
   50 EOF

  2.3. Set Permissions for Security

   1 chmod 600 .env
   2 sudo chown root:root .env

  3. Setup Repository dan Aplikasi

  3.1. Clone Repository

   1 cd ~/
   2 git clone https://github.com/Yoriyoi-drop/Domian_name_.nexa.git 
     production-app
   3 cd production-app

  3.2. Copy Konfigurasi Environment

   1 cp ~/production-config/.env ./

  4. Setup Docker Compose Production

  4.1. Buat Production Docker Compose

     1 cat > docker-compose.prod.yml << 'EOF'
     2 version: '3.8'
     3 
     4 services:
     5   # PostgreSQL Database
     6   postgres:
     7     image: postgres:15
     8     container_name: nexa-postgres-prod
     9     environment:
    10       POSTGRES_DB: ${POSTGRES_DB:-myproject_nexa_prod}
    11       POSTGRES_USER: ${POSTGRES_USER:-postgres}
    12       POSTGRES_PASSWORD: ${POSTGRES_PASSWORD:-postgres}
    13     ports:
    14       - "127.0.0.1:5432:5432"  # Hanya listen di localhost
    15     volumes:
    16       - postgres_data_prod:/var/lib/postgresql/data
    17       - ./backend/init.sql:/docker-entrypoint-initdb.d/init.sql
    18     networks:
    19       - nexa-network-prod
    20     healthcheck:
    21       test: ["CMD-SHELL", "pg_isready -U 
       ${POSTGRES_USER:-postgres}"]
    22       interval: 30s
    23       timeout: 10s
    24       retries: 3
    25     restart: unless-stopped
    26     security_opt:
    27       - no-new-privileges:true
    28 
    29   # Redis for caching and rate limiting
    30   redis:
    31     image: redis:7-alpine
    32     container_name: nexa-redis-prod
    33     command: redis-server --requirepass 
       ${REDIS_PASSWORD:-redis123} --appendonly yes
    34     ports:
    35       - "127.0.0.1:6379:6379"  # Hanya listen di localhost
    36     volumes:
    37       - redis_data_prod:/data
    38     networks:
    39       - nexa-network-prod
    40     healthcheck:
    41       test: ["CMD", "redis-cli", "--raw", "incr", "ping"]
    42       interval: 30s
    43       timeout: 10s
    44       retries: 3
    45     restart: unless-stopped
    46     security_opt:
    47       - no-new-privileges:true
    48 
    49   # Backend API Service
    50   backend:
    51     build:
    52       context: .
    53       dockerfile: Dockerfile.backend
    54       args:
    55         - JAR_FILE=backend/target/*.jar
    56     container_name: nexa-backend-prod
    57     environment:
    58       - SPRING_PROFILES_ACTIVE=prod
    59       - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/
       ${POSTGRES_DB:-myproject_nexa_prod}
    60       - SPRING_DATASOURCE_USERNAME=${POSTGRES_USER:-postgres}
    61       - SPRING_DATASOURCE_PASSWORD=${POSTGRES_PASSWORD:-postgres}
    62       - SPRING_REDIS_HOST=redis
    63       - SPRING_REDIS_PASSWORD=${REDIS_PASSWORD:-redis123}
    64       - JWT_SECRET=${JWT_SECRET}
    65       - SERVER_PORT=8080
    66     ports:
    67       - "127.0.0.1:8080:8080"
    68     networks:
    69       - nexa-network-prod
    70     depends_on:
    71       - postgres
    72       - redis
    73     restart: unless-stopped
    74     healthcheck:
    75       test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", 
       "http://localhost:8080/actuator/health"]
    76       interval: 30s
    77       timeout: 10s
    78       retries: 5
    79       start_period: 60s
    80     security_opt:
    81       - no-new-privileges:true
    82 
    83   # Frontend Service
    84   frontend:
    85     build:
    86       context: .
    87       dockerfile: Dockerfile.frontend
    88       args:
    89         - VITE_API_BASE_URL=http://localhost:8080/api/v1
    90     container_name: nexa-frontend-prod
    91     ports:
    92       - "3000:80"
    93     networks:
    94       - nexa-network-prod
    95     depends_on:
    96       - backend
    97     restart: unless-stopped
    98     healthcheck:
    99       test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", 
       "http://localhost"]
   100       interval: 30s
   101       timeout: 10s
   102       retries: 3
   103     security_opt:
   104       - no-new-privileges:true
   105 
   106   # Nginx Reverse Proxy
   107   nginx:
   108     image: nginx:alpine
   109     container_name: nexa-nginx-prod
   110     ports:
   111       - "80:80"
   112       - "443:443"
   113     volumes:
   114       - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
   115       - ./nginx/conf.d:/etc/nginx/conf.d:ro
   116       - /etc/ssl/certs:/etc/ssl/certs:ro
   117       - /etc/ssl/private:/etc/ssl/private:ro
   118       - nginx_logs_prod:/var/log/nginx
   119     networks:
   120       - nexa-network-prod
   121     depends_on:
   122       - backend
   123       - frontend
   124     restart: unless-stopped
   125     healthcheck:
   126       test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", 
       "http://localhost:80/health"]
   127       interval: 30s
   128       timeout: 10s
   129       retries: 3
   130     security_opt:
   131       - no-new-privileges:true
   132 
   133 volumes:
   134   postgres_data_prod:
   135     driver: local
   136   redis_data_prod:
   137     driver: local
   138   nginx_logs_prod:
   139     driver: local
   140 
   141 networks:
   142   nexa-network-prod:
   143     driver: bridge
   144     internal: false  # Bolehkan external access
   145 EOF

  5. Setup SSL Certificate (Opsional tapi Disarankan)

  5.1. Install Certbot

   1 sudo apt install certbot python3-certbot-nginx -y

  5.2. Dapatkan SSL Certificate

   1 sudo certbot --nginx -d yourdomain.com

  5.3. Atau gunakan certificate yang sudah ada

   1 sudo mkdir -p /etc/ssl/certs
   2 sudo mkdir -p /etc/ssl/private
   3 # Copy certificate files ke lokasi tersebut

  6. Build dan Deploy Aplikasi

  6.1. Build Aplikasi

    1 cd ~/production-app
    2 
    3 # Build backend
    4 cd backend
    5 ./mvnw clean package -DskipTests -Pprod
    6 cd ..
    7 
    8 # Build frontend
    9 cd frontend
   10 npm install
   11 npm run build
   12 cd ..

  6.2. Deploy dengan Docker Compose

   1 cd ~/production-app
   2 docker-compose -f docker-compose.prod.yml up -d

  7. Setup Monitoring dan Backup

  7.1. Setup Backup Otomatis

    1 cat > backup-script.sh << 'EOF'
    2 #!/bin/bash
    3 BACKUP_DIR="/opt/backups"
    4 DATE=$(date +%Y%m%d_%H%M%S)
    5 
    6 # Create backup directory
    7 mkdir -p $BACKUP_DIR
    8 
    9 # Backup PostgreSQL
   10 docker exec nexa-postgres-prod pg_dump -U postgres 
      myproject_nexa_prod > $BACKUP_DIR/db_backup_$DATE.sql
   11 
   12 # Compress backup
   13 tar -czf $BACKUP_DIR/backup_$DATE.tar.gz -C $BACKUP_DIR db_backup_
      $DATE.sql
   14 
   15 # Remove uncompressed file
   16 rm $BACKUP_DIR/db_backup_$DATE.sql
   17 
   18 # Keep only last 7 days of backups
   19 find $BACKUP_DIR -name "backup_*.tar.gz" -mtime +7 -delete
   20 
   21 echo "Backup completed: backup_$DATE.tar.gz"
   22 EOF
   23 
   24 chmod +x backup-script.sh
   25 sudo mv backup-script.sh /usr/local/bin/
   26 
   27 # Setup cron job untuk backup harian
   28 (crontab -l 2>/dev/null; echo "0 2 * * * 
      /usr/local/bin/backup-script.sh") | crontab -

  7.2. Setup Log Monitoring

    1 # Setup log rotation
    2 sudo cat > /etc/logrotate.d/nexa-app << 'EOF'
    3 /var/log/nexa/*.log {
    4     daily
    5     missingok
    6     rotate 52
    7     compress
    8     delaycompress
    9     notifempty
   10     create 644 root root
   11     postrotate
   12         docker restart nexa-nginx-prod
   13     endscript
   14 }
   15 EOF

  8. Setup Systemd Service (Opsional)

  8.1. Buat Service File

    1 sudo cat > /etc/systemd/system/nexa-app.service << 'EOF'
    2 [Unit]
    3 Description=domain.nexa Production Service
    4 After=docker.service
    5 Requires=docker.service
    6 
    7 [Service]
    8 Type=oneshot
    9 RemainAfterExit=yes
   10 WorkingDirectory=/home/$(whoami)/production-app
   11 ExecStart=/usr/local/bin/docker-compose -f docker-compose.prod.yml 
      up -d
   12 ExecStop=/usr/local/bin/docker-compose -f docker-compose.prod.yml 
      down
   13 ExecReload=/usr/local/bin/docker-compose -f docker-compose.prod.yml
      down && /usr/local/bin/docker-compose -f docker-compose.prod.yml up
      -d
   14 
   15 [Install]
   16 WantedBy=multi-user.target
   17 EOF
   18 
   19 sudo systemctl daemon-reload
   20 sudo systemctl enable nexa-app

  9. Verifikasi Deployment

  9.1. Cek Status Service

   1 docker-compose -f docker-compose.prod.yml ps

  9.2. Cek Log Aplikasi

   1 docker-compose -f docker-compose.prod.yml logs -f

  9.3. Cek Akses Aplikasi

   1 curl -I http://localhost:3000
   2 curl -I http://localhost:8080/actuator/health

  10. Upgrade Deployment (Ketika Rilis Baru)

  10.1. Backup Data

   1 /usr/local/bin/backup-script.sh

  10.2. Update Repository

    1 cd ~/production-app
    2 git pull origin master
    3 
    4 # Build ulang
    5 cd backend
    6 ./mvnw clean package -DskipTests -Pprod
    7 cd ..
    8 
    9 cd frontend
   10 npm install
   11 npm run build
   12 cd ..

  10.3. Restart Service

   1 docker-compose -f docker-compose.prod.yml down
   2 docker-compose -f docker-compose.prod.yml up -d

  Troubleshooting

  Jika Aplikasi Tidak Bisa Diakses

   1 # Cek status container
   2 docker ps
   3 
   4 # Cek log container
   5 docker logs <container_name>
   6 
   7 # Cek konfigurasi jaringan
   8 docker network ls

  Jika Ada Masalah dengan SSL

   1 # Cek certificate
   2 sudo ls -la /etc/ssl/certs/
   3 sudo ls -la /etc/ssl/private/
   4 
   5 # Cek konfigurasi nginx
   6 sudo nginx -t

  Security Best Practices

   1. Jangan pernah menyimpan .env di repository
   2. Gunakan password yang kuat untuk database
   3. Setup SSL certificate untuk semua traffic
   4. Gunakan firewall untuk membatasi akses
   5. Lakukan backup secara rutin
   6. Monitor log secara berkala
   7. Update sistem secara berkala

  Rollback Prosedur

  Jika terjadi masalah kritis, lakukan rollback:

   1 # Jika menggunakan systemd service
   2 sudo systemctl stop nexa-app
   3 # Revert ke backup terakhir
   4 sudo systemctl start nexa-app
   5 
   6 # Atau manual
   7 docker-compose -f docker-compose.prod.yml down
   8 git reset --hard <commit_hash_sebelumnya>
   9 docker-compose -f docker-compose.prod.yml up -d

  ---

  Catatan Penting: Pastikan untuk menyimpan .env file di tempat yang aman dan jangan
  pernah menyertakannya dalam repository publik. Ganti semua placeholder [isi_xxx]
  dengan nilai yang sesuai untuk lingkungan production Anda.