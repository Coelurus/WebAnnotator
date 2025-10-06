@echo off
REM Generate a secure 256-bit (32 byte) random key for JWT signing
REM Usage: generate-jwt-secret.bat

echo Generating secure JWT secret...

REM Generate random base64 string using PowerShell
for /f %%i in ('powershell -command "[System.Convert]::ToBase64String((1..32 | ForEach-Object {Get-Random -Minimum 0 -Maximum 256}))"') do set SECRET=%%i

echo Generated JWT Secret: %SECRET%
echo.
echo To use this secret:
echo 1. For development: set JWT_SECRET=%SECRET%
echo 2. For Docker: Add JWT_SECRET=%SECRET% to your .env file
echo 3. For production: Set JWT_SECRET environment variable in your deployment
echo.
echo Example .env file content:
echo JWT_SECRET=%SECRET%

pause