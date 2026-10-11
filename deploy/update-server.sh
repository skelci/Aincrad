#!/usr/bin/env bash
# Aincrad Server Update Script
# Pulls latest code, rebuilds, and restarts the server
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
SERVER_DIR="${SCRIPT_DIR}/server"

log() { echo "[UPDATE] $*"; }

# 1. Pull latest code
pull_latest() {
    log "Pulling latest code..."
    cd "${REPO_ROOT}"
    git pull origin "$(git branch --show-current)"
    log "Code updated."
}

# 2. Build and deploy
build_and_deploy() {
    log "Building and deploying..."
    "${SCRIPT_DIR}/build-and-deploy.sh"
}

# 3. Restart server
restart_server() {
    log "Restarting server..."
    if [[ -f "${SERVER_DIR}/server.pid" ]]; then
        "${SERVER_DIR}/stop.sh"
        sleep 5
    fi
    cd "${SERVER_DIR}"
    nohup ./start.sh > server.log 2>&1 &
    echo $! > server.pid
    log "Server restarted (PID: $(cat server.pid))"
}

main() {
    log "Starting server update..."
    pull_latest
    build_and_deploy
    restart_server
    log "Update complete."
}

main "$@"
