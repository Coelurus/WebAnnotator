# JWT Security Configuration

## Overview

This application now uses secure JWT token authentication with proper secret key management. The hardcoded "secret" key has been replaced with a secure, configurable system.

## Security Improvements

### 1. Dynamic Secret Key Generation
- If no JWT secret is provided, the application automatically generates a secure 256-bit random key
- Warning messages are logged when using auto-generated keys
- Keys are properly Base64 encoded for JWT use

### 2. Environment Variable Configuration
The JWT secret can now be configured via:
- `JWT_SECRET` environment variable (recommended)
- `spring.security.jwt.secret-key` application property

### 3. Refresh Token System
- Added refresh tokens with 7-day expiration
- Access tokens have 1-hour expiration for security
- Automatic token refresh on frontend
- Proper token revocation on logout

## Configuration

### Development
```bash
# Generate a secure secret (use the provided scripts)
./scripts/generate-jwt-secret.sh    # Linux/Mac
./scripts/generate-jwt-secret.bat   # Windows

# Set environment variable
export JWT_SECRET="your-generated-secret-here"

# Or set in application properties
spring.security.jwt.secret-key=your-generated-secret-here
```

### Production (Docker)
```yaml
# docker-compose.yml
environment:
  JWT_SECRET: ${JWT_SECRET}
```

```bash
# .env file (create this file in the same directory as docker-compose.yml)
JWT_SECRET=your-production-secret-here
```

### Production (Other Deployments)
Set the `JWT_SECRET` environment variable in your deployment configuration:
- Kubernetes: Use Secrets
- Cloud providers: Use their secret management services
- Traditional servers: Export the environment variable

## Security Best Practices

1. **Never commit secrets to git**
2. **Use different secrets for different environments**
3. **Rotate secrets regularly**
4. **Use your cloud provider's secret management service in production**
5. **Monitor for JWT-related security issues**

## Migration from Old System

If you're upgrading from the previous version with hardcoded secrets:

1. **Existing tokens will become invalid** when you change the secret
2. **Users will need to log in again** after the update
3. **Generate a new secret** using the provided scripts
4. **Set the JWT_SECRET environment variable** before starting the application

## Generating Secure Secrets

Use the provided scripts in the `scripts/` directory:

```bash
# For Unix-like systems (Linux, macOS, WSL)
./scripts/generate-jwt-secret.sh

# For Windows
./scripts/generate-jwt-secret.bat
```

Or generate manually:
```bash
# Using OpenSSL
openssl rand -base64 32

# Using Node.js
node -e "console.log(require('crypto').randomBytes(32).toString('base64'))"

# Using Python
python -c "import secrets, base64; print(base64.b64encode(secrets.token_bytes(32)).decode())"
```

## Troubleshooting

### Application fails to start
- Check that JWT_SECRET is properly set if you're not using auto-generation
- Ensure the secret is Base64 compatible

### Users can't log in after update
- This is expected when changing JWT secrets
- Users need to log in again with their credentials
- Old tokens are automatically invalidated

### Token refresh issues
- Check browser console for 401 errors
- Verify refresh tokens haven't expired (7-day limit)
- Clear browser localStorage if needed: `localStorage.clear()`

## Security Considerations

1. **Auto-generated keys are session-only**: They change on every application restart
2. **Use persistent secrets in production**: Set JWT_SECRET environment variable
3. **Monitor token usage**: Check application logs for authentication patterns
4. **Plan for secret rotation**: Have a process to update JWT secrets periodically