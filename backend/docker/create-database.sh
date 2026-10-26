#!/bin/bash
# Runs once after SQL Server is healthy. Creates the application database if it
# does not already exist. SA_PASSWORD is injected by Docker Compose.
set -e

echo "Creating database BookCrudDev (if not exists)..."

/opt/mssql-tools18/bin/sqlcmd \
  -S sqlserver \
  -U sa \
  -P "$SA_PASSWORD" \
  -C \
  -Q "IF NOT EXISTS (SELECT 1 FROM sys.databases WHERE name = N'BookCrudDev')
        CREATE DATABASE [BookCrudDev];"

echo "Database BookCrudDev is ready."
