#!/bin/bash

# setup-dev-env.sh
# Automated development environment setup script for MyProject.nexa

set -e  # Exit immediately if a command exits with a non-zero status

echo "=========================================="
echo "MyProject.nexa Development Environment Setup"
echo "=========================================="

# Check if running on supported platform
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    PLATFORM="linux"
elif [[ "$OSTYPE" == "darwin"* ]]; then
    PLATFORM="macos"
else
    echo "Unsupported platform: $OSTYPE"
    echo "This script supports Linux and macOS only"
    exit 1
fi

echo "Detected platform: $PLATFORM"

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to install a package using the appropriate package manager
install_package() {
    if command_exists apt-get; then
        sudo apt-get update && sudo apt-get install -y "$1"
    elif command_exists brew; then
        brew install "$1"
    elif command_exists yum; then
        sudo yum install -y "$1"
    else
        echo "Cannot install $1: unsupported package manager"
        exit 1
    fi
}

# Check and install required tools
echo -e "\nChecking for required tools..."

# Check Java
if command_exists java; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    if [[ "$JAVA_VERSION" < "21" ]]; then
        echo "Java 21 or higher is required. Current version: $JAVA_VERSION"
        if [[ "$PLATFORM" == "macos" ]]; then
            install_package "openjdk@21"
        elif [[ "$PLATFORM" == "linux" ]]; then
            install_package "openjdk-21-jdk"
        fi
    fi
else
    echo "Installing Java 21..."
    if [[ "$PLATFORM" == "macos" ]]; then
        install_package "openjdk@21"
        echo 'export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"' >> ~/.zshrc
    elif [[ "$PLATFORM" == "linux" ]]; then
        install_package "openjdk-21-jdk"
    fi
fi

# Check Node.js
if command_exists node; then
    NODE_VERSION=$(node -v | cut -d'v' -f2)
    NODE_MAJOR=$(echo $NODE_VERSION | cut -d'.' -f1)
    if [[ "$NODE_MAJOR" -lt 18 ]]; then
        echo "Node.js 18+ is required. Current version: $NODE_VERSION"
        install_package "node"
    fi
else
    echo "Installing Node.js..."
    install_package "node"
fi

# Check Docker
if ! command_exists docker; then
    echo "Installing Docker..."
    if [[ "$PLATFORM" == "macos" ]]; then
        echo "Please install Docker Desktop for Mac from: https://www.docker.com/products/docker-desktop"
        echo "After installation, please restart your terminal and run this script again."
        exit 1
    elif [[ "$PLATFORM" == "linux" ]]; then
        curl -fsSL https://get.docker.com -o get-docker.sh
        sh get-docker.sh
        sudo usermod -aG docker $USER
        echo "Added $USER to docker group. Please log out and log back in."
    fi
fi

# Check Git
if ! command_exists git; then
    echo "Installing Git..."
    install_package "git"
fi

# Check Maven
if ! command_exists mvn; then
    echo "Installing Maven..."
    install_package "maven"
fi

echo -e "\nAll required tools are installed."

# Clone the repository if it doesn't exist
REPO_DIR="myproject-nexa"
if [ ! -d "$REPO_DIR" ]; then
    echo -e "\nCloning the repository..."
    git clone https://github.com/your-org/myproject-nexa.git
else
    echo -e "\nRepository already exists. Updating..."
    cd "$REPO_DIR"
    git pull
    cd ..
fi

# Setup backend environment
echo -e "\nSetting up backend environment..."
cd "$REPO_DIR/backend"

# Create .env file from example if it doesn't exist
if [ ! -f ".env" ]; then
    if [ -f ".env.example" ]; then
        cp .env.example .env
        echo "Created .env file from example. Please update it with your settings."
    else
        echo "No .env.example file found. You may need to create .env manually."
    fi
fi

# Setup frontend environment
echo -e "\nSetting up frontend environment..."
cd "../frontend"

# Create .env file from example if it doesn't exist
if [ ! -f ".env" ]; then
    if [ -f ".env.example" ]; then
        cp .env.example .env
        echo "Created .env file from example. Please update it with your settings."
    else
        echo "No .env.example file found. You may need to create .env manually."
    fi
fi

# Install frontend dependencies
echo -e "\nInstalling frontend dependencies..."
npm install

# Return to main directory
cd ..

# Setup docker-compose environment
echo -e "\nSetting up docker-compose environment..."
if [ -f "docker-compose.yml" ]; then
    # Create .env file for docker-compose if it doesn't exist
    if [ ! -f ".env" ]; then
        if [ -f ".env.example" ]; then
            cp .env.example .env
            echo "Created .env file from example. Please update it with your settings."
        fi
    fi
else
    echo "docker-compose.yml not found in root directory."
fi

# Setup IDE configurations
echo -e "\nSetting up IDE configurations..."
cd ..

# Setup VS Code settings if the directory exists
if [ -d "$REPO_DIR/.vscode" ]; then
    echo "VS Code configuration detected in repository."
else
    mkdir -p "$REPO_DIR/.vscode"
    cat > "$REPO_DIR/.vscode/settings.json" << 'EOF'
{
    "java.compile.nullAnalysis.mode": "automatic",
    "java.configuration.runtimes": [
        {
            "name": "JavaSE-21",
            "path": "/path/to/java21"
        }
    ],
    "editor.formatOnSave": true,
    "editor.defaultFormatter": "esbenp.prettier-vscode",
    "java.format.settings.url": "https://raw.githubusercontent.com/google/styleguide/gh-pages/eclipse-java-google-style.xml",
    "java.format.settings.profile": "GoogleStyle",
    "files.insertFinalNewline": true,
    "files.trimFinalNewlines": true,
    "[javascript]": {
        "editor.defaultFormatter": "esbenp.prettier-vscode"
    },
    "[typescript]": {
        "editor.defaultFormatter": "esbenp.prettier-vscode"
    },
    "[json]": {
        "editor.defaultFormatter": "esbenp.prettier-vscode"
    },
    "[java]": {
        "editor.defaultFormatter": "redhat.java"
    }
}
EOF
    echo "Created VS Code settings file."
fi

# Setup IntelliJ configurations
if [ ! -d "$REPO_DIR/.idea" ]; then
    mkdir -p "$REPO_DIR/.idea"
    cat > "$REPO_DIR/.idea/codeStyles/Project.xml" << 'EOF'
<component name="ProjectCodeStyleConfiguration">
  <code_scheme name="Project" version="173">
    <option name="RIGHT_MARGIN" value="100" />
    <option name="FORMATTER_TAGS_ENABLED" value="true" />
  </code_scheme>
</component>
EOF
    mkdir -p "$REPO_DIR/.idea/codeStyles"
    cat > "$REPO_DIR/.idea/codeStyles/codeStyleConfig.xml" << 'EOF'
<component name="ProjectCodeStyleConfiguration">
  <state>
    <option name="PREFERRED_PROJECT_CODE_STYLE" value="GoogleStyle" />
  </state>
</component>
EOF
    echo "Created IntelliJ IDEA code style configurations."
fi

# Start the services using docker-compose
echo -e "\nStarting development services..."
cd "$REPO_DIR"

# Create docker network if it doesn't exist
if ! docker network ls | grep -q myproject-nexa; then
    docker network create myproject-nexa
fi

# Start services in the background
if [ -f "docker-compose.yml" ]; then
    docker-compose up -d
    echo -e "\nServices started in the background."
    echo "You can view logs with: docker-compose logs -f"
    echo "Backend will be available at: http://localhost:8080"
    echo "Frontend will be available at: http://localhost:3000"
else
    echo -e "\nNo docker-compose.yml found in the root directory."
fi

echo -e "\n=========================================="
echo "Development Environment Setup Complete!"
echo "=========================================="

echo -e "\nNext steps:"
echo "1. Check your .env files and update with appropriate values"
echo "2. For the backend: cd backend && ./mvnw spring-boot:run"
echo "3. For the frontend: cd frontend && npm run dev"
echo "4. Or use docker-compose to run everything: docker-compose up --build"
echo ""
echo "Useful commands:"
echo "- View logs: docker-compose logs -f"
echo "- Stop services: docker-compose down"
echo "- Run tests: cd backend && ./mvnw test && cd ../frontend && npm test"
echo ""
echo "For troubleshooting, please refer to the developer handbook:"
echo "$REPO_DIR/docs/handbook/README.md"