#!/usr/bin/env bash
# Aincrad Build and Deploy Script
# Builds mod, copies to server mods/, builds plugin, copies to server plugins/
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
SERVER_DIR="${SCRIPT_DIR}/server"
MODS_DIR="${SERVER_DIR}/mods"
PLUGINS_DIR="${SERVER_DIR}/plugins"

log() { echo "[BUILD-DEPLOY] $*"; }

# 1. Build the mod
build_mod() {
    log "Building Aincrad mod..."
    cd "${REPO_ROOT}"
    ./gradlew build --no-daemon
    log "Mod build complete."
}

# 2. Copy mod jar to server
deploy_mod() {
    log "Deploying mod jar to server..."
    mkdir -p "${MODS_DIR}"
    local mod_jar="${REPO_ROOT}/build/libs/aincrad-0.1.0.jar"
    if [[ -f "${mod_jar}" ]]; then
        cp "${mod_jar}" "${MODS_DIR}/"
        log "Mod jar deployed to ${MODS_DIR}"
    else
        log "ERROR: Mod jar not found at ${mod_jar}"
        exit 1
    fi
}

# 3. Build the Paper plugin
build_plugin() {
    log "Building Aincrad Paper plugin..."
    cd "${REPO_ROOT}"
    # Plugin is built as part of the main build or separately
    # Adjust path if plugin has its own build config
    local plugin_jar="${REPO_ROOT}/build/libs/aincrad-paper-0.1.0.jar"
    if [[ -f "${plugin_jar}" ]]; then
        log "Plugin build complete."
    else
        log "WARNING: Plugin jar not found. Skipping plugin deployment."
    fi
}

# 4. Copy plugin jar to server
deploy_plugin() {
    log "Deploying plugin jar to server..."
    mkdir -p "${PLUGINS_DIR}"
    local plugin_jar="${REPO_ROOT}/build/libs/aincrad-paper-0.1.0.jar"
    if [[ -f "${plugin_jar}" ]]; then
        cp "${plugin_jar}" "${PLUGINS_DIR}/"
        log "Plugin jar deployed to ${PLUGINS_DIR}"
    else
        log "WARNING: Plugin jar not found. Skipping."
    fi
}

main() {
    log "Starting build and deploy..."
    build_mod
    deploy_mod
    build_plugin
    deploy_plugin
    log "Build and deploy complete."
}

main "$@"
