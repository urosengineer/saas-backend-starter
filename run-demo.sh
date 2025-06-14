#!/bin/bash

set -e

echo
echo "Building and starting app via Docker Compose..."
docker-compose up --build
