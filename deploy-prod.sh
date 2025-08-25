#!/bin/bash

# Production deployment script for Picto Backend

set -e

echo "🚀 Starting Picto Backend production deployment..."

# Check if .env.prod exists
if [ ! -f .env.prod ]; then
    echo "❌ Error: .env.prod file not found!"
    echo "Please copy .env.prod.example to .env.prod and configure it."
    exit 1
fi

# Load production environment variables
export $(cat .env.prod | grep -v '^#' | xargs)

# Validate required environment variables
if [ -z "$DATABASE_PASSWORD" ]; then
    echo "❌ Error: DATABASE_PASSWORD must be set in .env.prod"
    exit 1
fi

echo "📦 Pulling latest Docker images..."
docker-compose -f compose.prod.yaml pull

echo "🏗️  Building and starting services..."
docker-compose -f compose.prod.yaml up -d

echo "⏳ Waiting for services to be healthy..."
# Wait for PostgreSQL
echo "Waiting for PostgreSQL..."
until docker-compose -f compose.prod.yaml exec postgres pg_isready -U ${DATABASE_USER:-postgres} -d ${DATABASE_NAME:-picto} > /dev/null 2>&1; do
    echo -n "."
    sleep 2
done
echo " ✅ PostgreSQL is ready"

# Wait for application
echo "Waiting for application..."
timeout=120
counter=0
until curl -f http://localhost:${APP_PORT:-8080}/actuator/health > /dev/null 2>&1; do
    if [ $counter -ge $timeout ]; then
        echo " ❌ Application failed to start within ${timeout} seconds"
        echo "Checking logs..."
        docker-compose -f compose.prod.yaml logs app
        exit 1
    fi
    echo -n "."
    sleep 2
    counter=$((counter + 2))
done
echo " ✅ Application is ready"

echo "🎉 Deployment completed successfully!"
echo "📊 Service status:"
docker-compose -f compose.prod.yaml ps

echo ""
echo "🔗 Application URLs:"
echo "   Health Check: http://localhost:${APP_PORT:-8080}/actuator/health"
echo "   Metrics:      http://localhost:${APP_PORT:-8080}/actuator/metrics"
if [ "${NGINX_PORT:-80}" != "8080" ]; then
    echo "   Nginx Proxy:  http://localhost:${NGINX_PORT:-80}"
fi

echo ""
echo "📝 Useful commands:"
echo "   View logs:    docker-compose -f compose.prod.yaml logs -f"
echo "   Stop:         docker-compose -f compose.prod.yaml down"
echo "   Restart:      docker-compose -f compose.prod.yaml restart"
