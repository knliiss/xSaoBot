#!/usr/bin/env bash
set -euo pipefail

# restart-app.sh
# One-command restart: build jar and recreate only the app service (leaves DB services untouched)
# Usage: ./restart-app.sh

COMPOSE_FILE="docker-compose.full.yml"
if [ ! -f "$COMPOSE_FILE" ]; then
  # fall back to default docker-compose.yml
  COMPOSE_FILE="docker-compose.yml"
fi

echo "[restart-app] Using compose file: $COMPOSE_FILE"

if ! command -v docker >/dev/null 2>&1; then
  echo "[restart-app] docker is not installed or not in PATH" >&2
  exit 2
fi

if ! command -v ./gradlew >/dev/null 2>&1; then
  echo "[restart-app] gradlew wrapper not found or not executable" >&2
  exit 2
fi

echo "[restart-app] Building application jar..."
./gradlew bootJar -x test

echo "[restart-app] Recreating app container (service: app) using $COMPOSE_FILE ..."
# Recreate only the app service, don't recreate DB services
docker compose -f "$COMPOSE_FILE" up --build --force-recreate --no-deps -d app

echo "[restart-app] Done. To follow logs: docker compose -f $COMPOSE_FILE logs -f app"
