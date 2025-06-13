#!/bin/bash

set -e

echo "Building the app (Maven)..."
./mvnw clean package -DskipTests

echo "Starting Docker Compose services..."
docker-compose up --build