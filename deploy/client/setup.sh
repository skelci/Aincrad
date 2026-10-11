#!/usr/bin/env bash
# Aincrad Client Setup Script
# Installs Fabric loader 0.19.5 + Fabric API, copies mod jar
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MODS_DIR="${HOME}/.minecraft/mods"
VERSIONS_DIR="${HOME}/.minecraft/versions"
FABRIC_VERSION="fabric-loader-0.19.5-26.3"
FABRIC_INSTALLER_URL="https://maven.fabricmc.net/net/fabricmc/fabric-installer/1.0.1/fabric-installer-1.0.1.jar"
MOD_JAR_SOURCE="${SCRIPT_DIR}/../../build/libs/aincrad-0.1.0.jar"
MOD_JAR_DEST="${MODS_DIR}/aincrad-0.1.0.jar"

log() { echo "[CLIENT SETUP] $*"; }

# 1. Check for Minecraft installation
check_minecraft() {
    log "Checking Minecraft installation..."
    if [[ ! -d "${HOME}/.minecraft" ]]; then
        log "ERROR: Minecraft not found at ~/.minecraft"
        log "Please install Minecraft 26.3 first."
        exit 1
    fi
    log "Minecraft directory found."
}

# 2. Install Fabric loader
install_fabric() {
    log "Installing Fabric loader 0.19.5 for MC 26.3..."
    local installer_jar="${SCRIPT_DIR}/fabric-installer.jar"

    if [[ ! -f "${installer_jar}" ]]; then
        curl -fsSL -o "${installer_jar}" "${FABRIC_INSTALLER_URL}"
    fi

    java -jar "${installer_jar}" client \
        --mcversion "26.3" \
        --loader "0.19.5" \
        --noprofile

    log "Fabric loader installed."
}

# 3. Copy mod jar
copy_mod() {
    log "Copying Aincrad mod jar..."
    mkdir -p "${MODS_DIR}"
    if [[ -f "${MOD_JAR_SOURCE}" ]]; then
        cp "${MOD_JAR_SOURCE}" "${MOD_JAR_DEST}"
        log "Mod jar copied to ${MODS_DIR}"
    else
        log "WARNING: Mod jar not found at ${MOD_JAR_SOURCE}"
        log "Build the mod first: ./gradlew build"
    fi
}

main() {
    log "Starting Aincrad client setup..."
    check_minecraft
    install_fabric
    copy_mod
    log "Client setup complete. Launch Minecraft with the Fabric profile."
}

main "$@"
