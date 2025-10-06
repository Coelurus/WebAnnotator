#!/bin/bash

# Generate a secure 256-bit (32 byte) random key for JWT signing
# Usage: ./generate-jwt-secret.sh

echo "Generating secure JWT secret..."
SECRET=$(openssl rand -base64 32)
echo "Generated JWT Secret: $SECRET"
echo ""
echo "To use this secret:"
echo "1. For development: export JWT_SECRET=\"$SECRET\""
echo "2. For Docker: Add JWT_SECRET=\"$SECRET\" to your .env file"
echo "3. For production: Set JWT_SECRET environment variable in your deployment"
echo ""
echo "Example .env file content:"
echo "JWT_SECRET=\"$SECRET\""