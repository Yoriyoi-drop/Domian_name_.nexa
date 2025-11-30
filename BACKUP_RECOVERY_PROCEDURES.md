# Database Backup and Recovery Procedures

## Automated Backup Configuration

### Cron Job Setup
To run automated backups, add the following entry to your system's crontab:

```bash
# Daily backup at 2:00 AM
0 2 * * * /path/to/backup-db.sh

# Weekly full backup on Sundays at 1:00 AM
0 1 * * 0 /path/to/backup-db.sh
```

### Environment Variables
The backup script uses the following environment variables:

- `DB_HOST` - Database host (default: localhost)
- `DB_PORT` - Database port (default: 5432)
- `DB_NAME` - Database name (default: myproject_nexa)
- `DB_USER` - Database user (default: postgres)
- `DB_PASSWORD` - Database password
- `BACKUP_DIR` - Directory to store backups (default: /backups)
- `RETENTION_DAYS` - Number of days to retain backups (default: 30)
- `ENCRYPTION_KEY` - Encryption key for backups (optional)
- `S3_BACKUP_BUCKET` - S3 bucket name for cloud backup (optional)

## Backup Verification

### Manual Backup Testing
To manually run a backup with specific parameters:

```bash
DB_PASSWORD=mypassword BACKUP_DIR=/custom/backups ./backup-db.sh
```

### Backup Integrity Check
The script automatically verifies backup integrity by checking file size.

## Recovery Procedures

### From Local Backup

1. Stop the application services:
   ```bash
   # Example for Kubernetes
   kubectl scale deployment/myproject-nexa-backend --replicas=0 -n myproject-nexa
   ```

2. Restore the database:
   ```bash
   # For encrypted backups
   gpg --decrypt backup_file.sql.gpg | PGPASSWORD=new_password psql -h db_host -U db_user -d db_name

   # For compressed backups
   gunzip -c backup_file.sql.gz | PGPASSWORD=new_password psql -h db_host -U db_user -d db_name

   # For regular backups
   PGPASSWORD=new_password psql -h db_host -U db_user -d db_name < backup_file.sql
   ```

3. Start the application services:
   ```bash
   kubectl scale deployment/myproject-nexa-backend --replicas=2 -n myproject-nexa
   ```

### From Cloud Storage (S3)

1. Download the backup from S3:
   ```bash
   aws s3 cp s3://your-bucket-name/backup_file.sql.gz ./
   ```

2. Follow the local recovery procedure.

## Automated Rollback Mechanism

### Kubernetes Deployment Rollback
The CI/CD pipeline includes automatic rollback for failed deployments:

```yaml
# In the deployment configuration
strategy:
  type: RollingUpdate
  rollingUpdate:
    maxUnavailable: 1
    maxSurge: 1
    # Additional rollback configurations
```

### Health Check Triggered Rollback
Deployment will automatically rollback if:
- Health checks fail after deployment
- Pod restart count exceeds threshold
- Response time exceeds acceptable limits

### Manual Rollback
To manually rollback to a previous deployment:

```bash
# Check rollout history
kubectl rollout history deployment/myproject-nexa-backend -n myproject-nexa

# Rollback to previous version
kubectl rollout undo deployment/myproject-nexa-backend -n myproject-nexa

# Rollback to specific revision
kubectl rollout undo deployment/myproject-nexa-backend --to-revision=2 -n myproject-nexa
```

## Monitoring and Alerts

### Backup Monitoring
- Monitor backup logs for completion status
- Set up alerts for failed backup jobs
- Check disk space on backup storage

### Deployment Monitoring
- Monitor deployment health in Kubernetes
- Set up alerts for failed deployments
- Track deployment metrics and performance

## Security Considerations

### Database Credentials
- Store database credentials in Kubernetes secrets
- Never hardcode credentials in scripts
- Use IAM roles for cloud storage access

### Backup Encryption
- Always encrypt sensitive backup data
- Use strong encryption keys
- Securely store encryption keys in a vault