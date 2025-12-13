#!/usr/bin/env bash
set -euo pipefail

# restart-app.sh
# One-command restart: ensure DB services are running, build jar and recreate only the app service
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

# find DB-like services in the compose file
DB_REGEX='(db|postgres|postgresql|mysql|mariadb|mongo|mongodb|redis)'
db_services=$(docker compose -f "$COMPOSE_FILE" config --services 2>/dev/null | grep -Ei "$DB_REGEX" || true)

wait_for_running() {
  local svc="$1"
  local timeout=${2:-30}
  local start_ts=$(date +%s)
  while :; do
    cid=$(docker compose -f "$COMPOSE_FILE" ps -q "$svc" 2>/dev/null || true)
    if [ -n "$cid" ]; then
      # prefer health status if available
      health=$(docker inspect -f '{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Running}}{{end}}' "$cid" 2>/dev/null || true)
      if [ "$health" = "healthy" ] || [ "$health" = "true" ]; then
        echo "[restart-app] $svc is up (status: $health)."
        return 0
      fi
    fi

    now_ts=$(date +%s)
    if [ $((now_ts - start_ts)) -ge "$timeout" ]; then
      echo "[restart-app] Timeout waiting for $svc to become ready." >&2
      return 1
    fi
    sleep 1
  done
}

if [ -z "$db_services" ]; then
  echo "[restart-app] No DB services detected in $COMPOSE_FILE."
else
  echo "[restart-app] Detected DB services: $db_services"
  for svc in $db_services; do
    echo "[restart-app] Checking DB service: $svc"
    cid=$(docker compose -f "$COMPOSE_FILE" ps -q "$svc" 2>/dev/null || true)

    if [ -n "$cid" ]; then
      running=$(docker inspect -f '{{.State.Running}}' "$cid" 2>/dev/null || echo "false")
      if [ "$running" = "true" ]; then
        echo "[restart-app] $svc container exists and is running."
      else
        echo "[restart-app] $svc container exists but is not running. Starting..."
        docker compose -f "$COMPOSE_FILE" up -d "$svc"
      fi
    else
      echo "[restart-app] $svc container not found. Starting..."
      docker compose -f "$COMPOSE_FILE" up -d "$svc"
    fi

    # wait a bit for the DB to be ready (uses health if present, falls back to running)
    if ! wait_for_running "$svc" 30; then
      echo "[restart-app] Warning: $svc did not become ready within timeout."
    fi
  done
fi

echo "[restart-app] Building application jar..."
./gradlew bootJar -x test

echo "[restart-app] Recreating app container (service: app) using $COMPOSE_FILE ..."
# Recreate only the app service, don't recreate DB services
docker compose -f "$COMPOSE_FILE" up --build --force-recreate --no-deps -d app

echo "[restart-app] Done. To follow logs: docker compose -f $COMPOSE_FILE logs -f app"
