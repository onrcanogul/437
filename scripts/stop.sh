#!/bin/bash
echo "Stopping all running containers..."
docker compose -f docker-compose.dev.yml down
docker compose -f docker-compose.prod.yml down
