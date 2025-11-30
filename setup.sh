#!/bin/bash

# Quick Setup Script for MyProject.nexa
# This script helps you quickly setup the development environment

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_header() {
    echo -e "${BLUE}================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}================================${NC}"
}

print_status() {
    echo -e "${GREEN}✓${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

# Check prerequisites
check_prerequisites() {
    print_header "Checking Prerequisites"
    
    local missing=0
    
    # Check Docker
    if command -v docker &> /dev/null; then
        print_status "Docker is installed ($(docker --version))"
    else
        print_error "Docker is not installed"
        missing=1
    fi
    
    # Check Docker Compose
    if command -v docker-compose &> /dev/null; then
        print_status "Docker Compose is installed ($(docker-compose --version))"
    else
        print_error "Docker Compose is not installed"
        missing=1
    fi
    
    # Check Git
    if command -v git &> /dev/null; then
        print_status "Git is installed ($(git --version))"
    else
        print_error "Git is not installed"
        missing=1
    fi
    
    # Check Node.js (optional for local development)
    if command -v node &> /dev/null; then
        print_status "Node.js is installed ($(node --version))"
    else
        print_warning "Node.js is not installed (optional for local development)"
    fi
    
    # Check Java (optional for local development)
    if command -v java &> /dev/null; then
        print_status "Java is installed ($(java --version 2>&1 | head -n 1))"
    else
        print_warning "Java is not installed (optional for local development)"
    fi
    
    if [ $missing -eq 1 ]; then
        print_error "Please install missing prerequisites before continuing"
        exit 1
    fi
    
    echo ""
}

# Setup environment
setup_environment() {
    print_header "Setting Up Environment"
    
    # Ask for environment type
    echo "Which environment do you want to setup?"
    echo "1) Development (default)"
    echo "2) Staging"
    echo "3) Production"
    read -p "Enter choice [1-3]: " env_choice
    
    case $env_choice in
        2)
            ENV_FILE=".env.staging.template"
            ENV_NAME="staging"
            ;;
        3)
            ENV_FILE=".env.production.template"
            ENV_NAME="production"
            ;;
        *)
            ENV_FILE=".env.development.template"
            ENV_NAME="development"
            ;;
    esac
    
    if [ -f ".env" ]; then
        print_warning ".env file already exists"
        read -p "Do you want to overwrite it? (y/n): " overwrite
        if [ "$overwrite" != "y" ]; then
            print_status "Keeping existing .env file"
            return
        fi
    fi
    
    cp "$ENV_FILE" .env
    print_status "Created .env file from $ENV_FILE"
    
    if [ "$ENV_NAME" != "development" ]; then
        print_warning "Please update .env file with your actual configuration values!"
        read -p "Press Enter to edit .env file now..." 
        ${EDITOR:-nano} .env
    fi
    
    echo ""
}

# Generate SSL certificates
generate_ssl() {
    print_header "SSL Certificates"
    
    if [ -f "ssl/certs/server.crt" ]; then
        print_status "SSL certificates already exist"
        return
    fi
    
    read -p "Do you want to generate self-signed SSL certificates? (y/n): " gen_ssl
    
    if [ "$gen_ssl" = "y" ]; then
        if [ -f "generate-certs.sh" ]; then
            chmod +x generate-certs.sh
            ./generate-certs.sh
            print_status "SSL certificates generated"
        else
            print_warning "generate-certs.sh not found, skipping SSL generation"
        fi
    fi
    
    echo ""
}

# Build application
build_application() {
    print_header "Building Application"
    
    read -p "Do you want to build the application now? (y/n): " build_app
    
    if [ "$build_app" != "y" ]; then
        print_status "Skipping build"
        return
    fi
    
    print_status "Building Docker images..."
    docker-compose build
    
    print_status "Build completed successfully"
    echo ""
}

# Start services
start_services() {
    print_header "Starting Services"
    
    read -p "Do you want to start the services now? (y/n): " start_svc
    
    if [ "$start_svc" != "y" ]; then
        print_status "Skipping service start"
        return
    fi
    
    print_status "Starting Docker containers..."
    docker-compose up -d
    
    print_status "Waiting for services to be ready..."
    sleep 10
    
    # Check service status
    docker-compose ps
    
    echo ""
}

# Display access information
show_access_info() {
    print_header "Access Information"
    
    echo -e "${GREEN}Application URLs:${NC}"
    echo "  Frontend:    http://localhost:3000"
    echo "  Backend API: http://localhost:8080/api/v1"
    echo "  API Docs:    http://localhost:8080/api/v1/swagger-ui.html"
    echo "  PGAdmin:     http://localhost:5050"
    echo ""
    
    echo -e "${GREEN}Default Credentials:${NC}"
    echo "  PGAdmin:"
    echo "    Email:    admin@myproject.nexa"
    echo "    Password: admin123"
    echo ""
    
    echo -e "${GREEN}Useful Commands:${NC}"
    echo "  View logs:           docker-compose logs -f"
    echo "  Stop services:       docker-compose down"
    echo "  Restart services:    docker-compose restart"
    echo "  Rebuild:             docker-compose up -d --build"
    echo "  Database backup:     ./backup-db.sh backup"
    echo "  Deploy:              ./deploy.sh deploy"
    echo ""
}

# Setup git hooks (optional)
setup_git_hooks() {
    print_header "Git Hooks Setup"
    
    read -p "Do you want to setup git hooks for code quality? (y/n): " setup_hooks
    
    if [ "$setup_hooks" != "y" ]; then
        return
    fi
    
    # Create pre-commit hook
    cat > .git/hooks/pre-commit << 'EOF'
#!/bin/bash
# Pre-commit hook for MyProject.nexa

echo "Running pre-commit checks..."

# Check for .env files
if git diff --cached --name-only | grep -q "^\.env$"; then
    echo "Error: Attempting to commit .env file!"
    echo "Please remove .env from staging area"
    exit 1
fi

# Check for secrets in code
if git diff --cached | grep -i "password\|secret\|api_key" | grep -v "# password"; then
    echo "Warning: Possible secrets detected in commit"
    read -p "Continue anyway? (y/n): " continue
    if [ "$continue" != "y" ]; then
        exit 1
    fi
fi

echo "Pre-commit checks passed!"
EOF
    
    chmod +x .git/hooks/pre-commit
    print_status "Git hooks setup completed"
    echo ""
}

# Create helpful aliases
create_aliases() {
    print_header "Helpful Aliases"
    
    echo "Add these aliases to your ~/.bashrc or ~/.zshrc:"
    echo ""
    echo "alias nexa-start='docker-compose up -d'"
    echo "alias nexa-stop='docker-compose down'"
    echo "alias nexa-logs='docker-compose logs -f'"
    echo "alias nexa-restart='docker-compose restart'"
    echo "alias nexa-rebuild='docker-compose up -d --build'"
    echo "alias nexa-backup='./backup-db.sh backup'"
    echo "alias nexa-deploy='./deploy.sh deploy'"
    echo ""
}

# Main setup process
main() {
    clear
    echo -e "${BLUE}"
    cat << "EOF"
    __  __      ____            _           _     _   _                 
   |  \/  |_   |  _ \ _ __ ___ (_) ___  ___| |_  | \ | | _____  ____ _ 
   | |\/| | | | | |_) | '__/ _ \| |/ _ \/ __| __| |  \| |/ _ \ \/ / _` |
   | |  | | |_| |  __/| | | (_) | |  __/ (__| |_ _| |\  |  __/>  < (_| |
   |_|  |_|\__, |_|   |_|  \___// |\___|\___|\__(_)_| \_|\___/_/\_\__,_|
           |___/              |__/                                       
EOF
    echo -e "${NC}"
    echo "Welcome to MyProject.nexa Setup!"
    echo ""
    
    check_prerequisites
    setup_environment
    generate_ssl
    build_application
    start_services
    show_access_info
    setup_git_hooks
    create_aliases
    
    print_header "Setup Complete!"
    echo -e "${GREEN}Your development environment is ready!${NC}"
    echo ""
    echo "Next steps:"
    echo "1. Review and update .env file if needed"
    echo "2. Access the application at http://localhost:3000"
    echo "3. Check the API documentation at http://localhost:8080/api/v1/swagger-ui.html"
    echo "4. Read DEPLOYMENT_GUIDE.md for more information"
    echo ""
    echo "Happy coding! 🚀"
}

# Run main function
main
