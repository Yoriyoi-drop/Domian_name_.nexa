#!/bin/bash

# Deployment script for MyProject.nexa
# This script automates the deployment process to a production server

set -e  # Exit immediately if a command exits with a non-zero status

# Configuration
REPO_URL="https://github.com/your-username/myproject.nexa.git"
PROJECT_DIR="/opt/myproject-nexa"
BACKUP_DIR="/opt/myproject-nexa-backup"
LOG_FILE="/var/log/myproject-nexa-deploy.log"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Logging function
log() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" | tee -a $LOG_FILE
}

# Print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1" | tee -a $LOG_FILE
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1" | tee -a $LOG_FILE
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1" | tee -a $LOG_FILE
}

# Check if running as root
if [[ $EUID -eq 0 ]]; then
    print_warning "This script should not be run as root. Please run as a regular user with sudo access."
    exit 1
fi

# Function to check prerequisites
check_prerequisites() {
    print_status "Checking prerequisites..."

    # Check for required tools
    local tools=("docker" "docker-compose" "git" "curl" "openssl")
    for tool in "${tools[@]}"; do
        if ! command -v $tool &> /dev/null; then
            print_error "$tool is not installed. Please install it first."
            exit 1
        fi
    done

    print_status "All prerequisites are installed."
}

# Function to pull latest code
pull_latest_code() {
    print_status "Pulling latest code from repository..."

    if [ -d "$PROJECT_DIR" ]; then
        cd $PROJECT_DIR
        git fetch origin
        git reset --hard origin/main
        print_status "Code updated successfully."
    else
        git clone $REPO_URL $PROJECT_DIR
        cd $PROJECT_DIR
        print_status "Code cloned successfully."
    fi
}

# Function to backup current deployment
backup_current() {
    print_status "Creating backup of current deployment..."

    if [ -d "$PROJECT_DIR" ]; then
        if [ -d "$BACKUP_DIR" ]; then
            rm -rf $BACKUP_DIR
        fi
        
        cp -r $PROJECT_DIR $BACKUP_DIR
        print_status "Backup created at $BACKUP_DIR"
    else
        print_warning "No previous deployment found to backup."
    fi
}

# Function to build and deploy
deploy() {
    print_status "Starting deployment process..."

    # Navigate to project directory
    cd $PROJECT_DIR

    # Generate or update environment files if needed
    if [ ! -f ".env" ]; then
        print_warning "Environment file not found. Creating default .env file..."
        cat > .env << EOF
# Database Configuration
DATABASE_URL=postgresql://localhost:5432/myproject_nexa
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_secure_password
DB_PORT=5432

# JWT Configuration
JWT_SECRET=your_very_secure_jwt_secret_key_change_this_in_production
JWT_EXPIRATION=86400000

# Application Configuration
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080

# Frontend Configuration
VITE_API_BASE_URL=https://api.myproject.nexa
EOF
        print_status "Default .env file created. Please update with your actual values."
    fi

    # Build and start services
    print_status "Building and starting Docker containers..."
    docker-compose down
    docker-compose build
    docker-compose up -d

    # Wait for services to be ready
    print_status "Waiting for services to start..."
    sleep 30

    # Check if services are running
    if docker-compose ps | grep -q "Up"; then
        print_status "Deployment successful!"
    else
        print_error "Deployment failed! Services are not running properly."
        exit 1
    fi
}

# Function to run health checks
run_health_checks() {
    print_status "Running health checks..."

    # Check if frontend is accessible
    if curl -s --connect-timeout 10 https://myproject.nexa/health > /dev/null; then
        print_status "Frontend health check passed."
    else
        print_warning "Frontend health check failed."
    fi

    # Check if backend is accessible
    if curl -s --connect-timeout 10 https://api.myproject.nexa/actuator/health > /dev/null; then
        print_status "Backend health check passed."
    else
        print_warning "Backend health check failed."
    fi
}

# Function to rollback
rollback() {
    print_warning "Rolling back to previous version..."

    if [ -d "$BACKUP_DIR" ]; then
        if [ -d "$PROJECT_DIR" ]; then
            rm -rf $PROJECT_DIR
        fi
        cp -r $BACKUP_DIR $PROJECT_DIR
        cd $PROJECT_DIR
        docker-compose down
        docker-compose up -d
        print_status "Rollback completed."
    else
        print_error "No backup found to rollback to."
        exit 1
    fi
}

# Main execution
main() {
    print_status "Starting MyProject.nexa deployment..."

    check_prerequisites
    backup_current
    pull_latest_code
    deploy
    run_health_checks

    print_status "Deployment completed successfully!"
    print_status "Application is now running at https://myproject.nexa"
    print_status "API is available at https://api.myproject.nexa"
}

# Trap to handle errors and rollback if needed
trap 'print_error "Deployment failed at line $LINENO. Consider running rollback if needed." ; exit 1' ERR

# Parse command line arguments
case "${1:-}" in
    "deploy")
        main
        ;;
    "rollback")
        rollback
        ;;
    "health-check")
        run_health_checks
        ;;
    *)
        echo "Usage: $0 {deploy|rollback|health-check}"
        echo "  deploy      - Deploy the application"
        echo "  rollback    - Rollback to previous version"
        echo "  health-check - Run health checks"
        exit 1
        ;;
esac