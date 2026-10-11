#!/usr/bin/env bash
# Aincrad Server Setup Script
# Installs Java 25, downloads Paper 26.3, installs Fabric server, copies mod jar
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SERVER_DIR="${SCRIPT_DIR}"
MODS_DIR="${SERVER_DIR}/mods"
PLUGINS_DIR="${SERVER_DIR}/plugins"

PAPER_VERSION="26.3"
PAPER_BUILD="latest"
PAPER_JAR="paper-${PAPER_VERSION}-${PAPER_BUILD}.jar"
PAPER_URL="https://api.papermc.io/v2/projects/paper/versions/${PAPER_VERSION}/builds/${PAPER_BUILD}/downloads/${PAPER_JAR}"

FABRIC_LOADER_VERSION="0.19.5"
FABRIC_INSTALLER_URL="https://maven.fabricmc.net/net/fabricmc/fabric-installer/1.0.1/fabric-installer-1.0.1.jar"

MOD_JAR_SOURCE="${SCRIPT_DIR}/../../build/libs/aincrad-0.1.0.jar"
MOD_JAR_DEST="${MODS_DIR}/aincrad-0.1.0.jar"

log() { echo "[SETUP] $*"; }

# 1. Install Java 25
install_java() {
    log "Checking Java 25..."
    if command -v java &>/dev/null; then
        JAVA_VER=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
        if [[ "${JAVA_VER}" == "25" ]]; then
            log "Java 25 already installed."
            return
        fi
    fi

    log "Installing Java 25..."
    if command -v apt-get &>/dev/null; then
        sudo apt-get update -qq
        sudo apt-get install -y openjdk-25-jdk-headless
    elif command -v yum &>/dev/null; then
        sudo yum install -y java-25-openjdk-devel
    elif command -v dnf &>/dev/null; then
        sudo dnf install -y java-25-openjdk-devel
    else
        log "ERROR: No supported package manager found. Install Java 25 manually."
        exit 1
    fi
    log "Java 25 installed."
}

# 2. Download Paper 26.3
download_paper() {
    log "Downloading Paper ${PAPER_VERSION}..."
    mkdir -p "${SERVER_DIR}"
    if [[ -f "${SERVER_DIR}/${PAPER_JAR}" ]]; then
        log "Paper jar already exists, skipping download."
    else
        curl -fsSL -o "${SERVER_DIR}/${PAPER_JAR}" "${PAPER_URL}"
        log "Paper downloaded: ${PAPER_JAR}"
    fi
}

# 3. Install Fabric server
install_fabric() {
    log "Installing Fabric server (loader ${FABRIC_LOADER_VERSION})..."
    local installer_jar="${SERVER_DIR}/fabric-installer.jar"

    if [[ ! -f "${installer_jar}" ]]; then
        curl -fsSL -o "${installer_jar}" "${FABRIC_INSTALLER_URL}"
    fi

    cd "${SERVER_DIR}"
    java -jar "${installer_jar}" server \
        --mcversion "${PAPER_VERSION}" \
        --loader "${FABRIC_LOADER_VERSION}" \
        --downloadMinecraft \
        --noprofile

    log "Fabric server installed."
}

# 4. Copy mod jar
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

# 5. Accept EULA
accept_eula() {
    log "Accepting EULA..."
    echo "eula=true" > "${SERVER_DIR}/eula.txt"
}

main() {
    log "Starting Aincrad server setup..."
    install_java
    download_paper
    install_fabric
    copy_mod
    accept_eula
    log "Setup complete. Run ./start.sh to launch the server."
}

main "$@"
