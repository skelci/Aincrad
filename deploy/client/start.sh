#!/usr/bin/env bash
# Aincrad Client Start Script
# Launches Minecraft with Fabric profile
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

FABRIC_VERSION="fabric-loader-0.19.5-26.3"
MINECRAFT_LAUNCHER=""

# Detect launcher
detect_launcher() {
    if command -v minecraft-launcher &>/dev/null; then
        MINECRAFT_LAUNCHER="minecraft-launcher"
    elif command -v prismlauncher &>/dev/null; then
        MINECRAFT_LAUNCHER="prismlauncher"
    elif command -v "PolyMC" &>/dev/null; then
        MINECRAFT_LAUNCHER="PolyMC"
    elif [[ -f "/usr/bin/minecraft-launcher" ]]; then
        MINECRAFT_LAUNCHER="/usr/bin/minecraft-launcher"
    else
        echo "[CLIENT] ERROR: No Minecraft launcher found."
        echo "[CLIENT] Please install a launcher (minecraft-launcher, Prism Launcher, or PolyMC)."
        exit 1
    fi
}

launch() {
    echo "[CLIENT] Launching Minecraft with Fabric..."
    if [[ "${MINECRAFT_LAUNCHER}" == "minecraft-launcher" ]]; then
        minecraft-launcher --launch "${FABRIC_VERSION}" &
    elif [[ "${MINECRAFT_LAUNCHER}" == "prismlauncher" ]]; then
        prismlauncher --launch "${FABRIC_VERSION}" &
    elif [[ "${MINECRAFT_LAUNCHER}" == "PolyMC" ]]; then
        PolyMC --launch "${FABRIC_VERSION}" &
    fi
}

detect_launcher
launch
