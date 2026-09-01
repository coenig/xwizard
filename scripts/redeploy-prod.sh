#!/usr/bin/env bash
# Rebuilds the WAR and redeploys the production stack (docker-compose.prod.yml).
# Usage: scripts/redeploy-prod.sh [--skip-pull] [--skip-build]
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(dirname "$SCRIPT_DIR")"
COMPOSE_FILE="$REPO_ROOT/docker-compose.prod.yml"

SKIP_PULL=false
SKIP_BUILD=false
for arg in "$@"; do
    case "$arg" in
        --skip-pull) SKIP_PULL=true ;;
        --skip-build) SKIP_BUILD=true ;;
        *) echo "Unknown option: $arg" >&2; exit 1 ;;
    esac
done

cd "$REPO_ROOT"

if [[ "$SKIP_PULL" == false ]]; then
    echo "==> Pulling latest source..."
    git pull
fi

if [[ "$SKIP_BUILD" == false ]]; then
    echo "==> Building WAR (mvn -U clean package)..."
    mvn -U clean package
fi

echo "==> Rebuilding and recreating the app container..."
docker compose -f "$COMPOSE_FILE" up -d --build app

echo "==> Tailing app logs (Ctrl+C to stop tailing; the container keeps running)..."
docker compose -f "$COMPOSE_FILE" logs -f --tail=100 app
