#!/bin/bash

# Generate SSL certificates for development
# This script creates self-signed certificates for myproject.nexa and api.myproject.nexa

echo "Generating SSL certificates for myproject.nexa..."

# Create the SSL directory if it doesn't exist
mkdir -p ssl/certs ssl/private

# Generate private key
openssl genrsa -out ssl/private/myproject.nexa.key 2048

# Generate certificate signing request
openssl req -new -key ssl/private/myproject.nexa.key -out ssl/certs/myproject.nexa.csr -subj "/C=US/ST=State/L=City/O=Organization/CN=myproject.nexa"

# Generate self-signed certificate
openssl x509 -req -in ssl/certs/myproject.nexa.csr -signkey ssl/private/myproject.nexa.key -out ssl/certs/myproject.nexa.crt -days 365

# Set proper permissions
chmod 600 ssl/private/myproject.nexa.key
chmod 644 ssl/certs/myproject.nexa.crt

echo "SSL certificates generated successfully!"
echo "Certificate: ssl/certs/myproject.nexa.crt"
echo "Private Key: ssl/private/myproject.nexa.key"

# Add entry to hosts file (requires sudo)
if ! grep -q "myproject.nexa" /etc/hosts; then
    echo "Adding myproject.nexa to /etc/hosts..."
    echo "127.0.0.1 myproject.nexa" | sudo tee -a /etc/hosts
    echo "127.0.0.1 api.myproject.nexa" | sudo tee -a /etc/hosts
fi

echo "Setup complete! You can now access:"
echo "https://myproject.nexa"
echo "https://api.myproject.nexa"