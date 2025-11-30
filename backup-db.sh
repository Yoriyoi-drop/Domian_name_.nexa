#!/bin/bash

# backup-db.sh - Automated database backup script for MyProject.nexa
# This script creates automated backups of the PostgreSQL database

set -e  # Exit immediately if a command exits with a non-zero status

# Configuration
DB_HOST=${DB_HOST:-"localhost"}
DB_PORT=${DB_PORT:-"5432"}
DB_NAME=${DB_NAME:-"myproject_nexa"}
DB_USER=${DB_USER:-"postgres"}
DB_PASSWORD=${DB_PASSWORD:-"postgres"}
BACKUP_DIR=${BACKUP_DIR:-"/backups"}
RETENTION_DAYS=${RETENTION_DAYS:-30}
ENCRYPTION_KEY=${ENCRYPTION_KEY:-""}

# Generate backup filename with timestamp
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="${BACKUP_DIR}/myproject-nexa_${DB_NAME}_${TIMESTAMP}.sql"
ENCRYPTED_BACKUP_FILE="${BACKUP_FILE}.gpg"

echo "Starting backup process for database: ${DB_NAME}"
echo "Backup file: ${BACKUP_FILE}"

# Create backup directory if it doesn't exist
mkdir -p "${BACKUP_DIR}"

# Create database backup using pg_dump
echo "Creating database dump..."
PGPASSWORD="${DB_PASSWORD}" pg_dump -h "${DB_HOST}" -p "${DB_PORT}" -U "${DB_USER}" -d "${DB_NAME}" > "${BACKUP_FILE}"

# Check if backup was successful
if [ $? -eq 0 ]; then
    echo "Database backup created successfully"

    # Encrypt the backup if encryption key is provided
    if [ -n "${ENCRYPTION_KEY}" ] && command -v gpg >/dev/null 2>&1; then
        echo "Encrypting backup file..."
        echo "${ENCRYPTION_KEY}" | gpg --batch --passphrase-fd 0 --symmetric --cipher-algo AES256 --output "${ENCRYPTED_BACKUP_FILE}" "${BACKUP_FILE}"
        
        # Remove unencrypted backup file after encryption
        rm "${BACKUP_FILE}"
        echo "Backup encrypted and saved as: ${ENCRYPTED_BACKUP_FILE}"
    else
        echo "Backup completed without encryption (gpg not available or encryption key not provided)"
    fi

    # Compress the backup if not encrypted
    if [ -z "${ENCRYPTION_KEY}" ]; then
        echo "Compressing backup file..."
        gzip "${BACKUP_FILE}"
        echo "Backup compressed and saved as: ${BACKUP_FILE}.gz"
    fi

    # Remove old backups (older than retention period)
    echo "Removing backups older than ${RETENTION_DAYS} days..."
    find "${BACKUP_DIR}" -name "myproject-nexa_${DB_NAME}_*.sql*" -type f -mtime +${RETENTION_DAYS} -delete

    # Verify backup integrity by checking file size
    if [ -n "${ENCRYPTION_KEY}" ]; then
        BACKUP_FILE_SIZE=$(stat -c%s "${ENCRYPTED_BACKUP_FILE}" 2>/dev/null || stat -f%z "${ENCRYPTED_BACKUP_FILE}" 2>/dev/null)
    else
        BACKUP_FILE_SIZE=$(stat -c%s "${BACKUP_FILE}.gz" 2>/dev/null || stat -f%z "${BACKUP_FILE}.gz" 2>/dev/null)
    fi

    if [ "${BACKUP_FILE_SIZE:-0}" -gt 0 ]; then
        echo "Backup verification successful - backup file has content"
        
        # Optional: Upload to cloud storage (AWS S3 example)
        if command -v aws >/dev/null 2>&1 && [ -n "${S3_BACKUP_BUCKET}" ]; then
            echo "Uploading backup to S3 bucket: ${S3_BACKUP_BUCKET}"
            if [ -n "${ENCRYPTION_KEY}" ]; then
                aws s3 cp "${ENCRYPTED_BACKUP_FILE}" "s3://${S3_BACKUP_BUCKET}/${ENCRYPTED_BACKUP_FILE##*/}"
            else
                aws s3 cp "${BACKUP_FILE}.gz" "s3://${S3_BACKUP_BUCKET}/${BACKUP_FILE##*/}.gz"
            fi
        fi
        
        echo "Backup process completed successfully"
    else
        echo "ERROR: Backup file is empty or doesn't exist"
        exit 1
    fi
else
    echo "ERROR: Database backup failed"
    exit 1
fi