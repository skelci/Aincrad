#!/usr/bin/env bash
# Aincrad Server Backup Script
# Backs up world data, player data, and plugin configs
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SERVER_DIR="${SCRIPT_DIR}/server"
BACKUP_DIR="${SCRIPT_DIR}/backups"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_NAME="aincrad_backup_${TIMESTAMP}"
BACKUP_PATH="${BACKUP_DIR}/${BACKUP_NAME}"

log() { echo "[BACKUP] $*"; }

# 1. Create backup directory
create_backup_dir() {
    log "Creating backup directory..."
    mkdir -p "${BACKUP_PATH}"
}

# 2. Backup world data
backup_worlds() {
    log "Backing up worlds..."
    for world in world world_nether world_the_end; do
        if [[ -d "${SERVER_DIR}/${world}" ]]; then
            cp -r "${SERVER_DIR}/${world}" "${BACKUP_PATH}/${world}"
            log "Backed up ${world}"
        fi
    done
}

# 3. Backup player data
backup_players() {
    log "Backing up player data..."
    if [[ -d "${SERVER_DIR}/world/playerdata" ]]; then
        cp -r "${SERVER_DIR}/world/playerdata" "${BACKUP_PATH}/playerdata"
    fi
    if [[ -d "${SERVER_DIR}/world/stats" ]]; then
        cp -r "${SERVER_DIR}/world/stats" "${BACKUP_PATH}/stats"
    fi
    if [[ -d "${SERVER_DIR}/world/advancements" ]]; then
        cp -r "${SERVER_DIR}/world/advancements" "${BACKUP_PATH}/advancements"
    fi
    log "Player data backed up."
}

# 4. Backup plugin configs
backup_configs() {
    log "Backing up plugin configs..."
    if [[ -d "${SERVER_DIR}/plugins" ]]; then
        cp -r "${SERVER_DIR}/plugins" "${BACKUP_PATH}/plugins"
    fi
    if [[ -f "${SERVER_DIR}/server.properties" ]]; then
        cp "${SERVER_DIR}/server.properties" "${BACKUP_PATH}/"
    fi
    log "Configs backed up."
}

# 5. Create archive
create_archive() {
    log "Creating backup archive..."
    cd "${BACKUP_DIR}"
    tar -czf "${BACKUP_NAME}.tar.gz" "${BACKUP_NAME}"
    rm -rf "${BACKUP_NAME}"
    log "Archive created: ${BACKUP_DIR}/${BACKUP_NAME}.tar.gz"
}

# 6. Clean old backups (keep last 7)
clean_old_backups() {
    log "Cleaning old backups (keeping last 7)..."
    cd "${BACKUP_DIR}"
    ls -t aincrad_backup_*.tar.gz 2>/dev/null | tail -n +8 | xargs -r rm -f
    log "Old backups cleaned."
}

main() {
    log "Starting backup..."
    create_backup_dir
    backup_worlds
    backup_players
    backup_configs
    create_archive
    clean_old_backups
    log "Backup complete: ${BACKUP_DIR}/${BACKUP_NAME}.tar.gz"
}

main "$@"
