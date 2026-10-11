#!/usr/bin/env bash
# Aincrad Server Start Script
# Starts the Paper + Fabric server with Aikar's JVM flags
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${SCRIPT_DIR}"

PAPER_JAR=$(ls paper-*.jar 2>/dev/null | head -1)
if [[ -z "${PAPER_JAR}" ]]; then
    echo "[START] ERROR: No Paper jar found. Run setup.sh first."
    exit 1
fi

# Aikar's flags for optimal GC performance
JAVA_FLAGS=(
    -Xms4G
    -Xmx4G
    -XX:+UseG1GC
    -XX:+ParallelRefProcEnabled
    -XX:MaxGCPauseMillis=200
    -XX:+UnlockExperimentalVMOptions
    -XX:+DisableExplicitGC
    -XX:+AlwaysPreTouch
    -XX:G1NewSizePercent=30
    -XX:G1MaxNewSizePercent=40
    -XX:G1HeapRegionSize=8M
    -XX:G1ReservePercent=20
    -XX:G1HeapWastePercent=5
    -XX:G1MixedGCCountTarget=4
    -XX:InitiatingHeapOccupancyPercent=15
    -XX:G1MixedGCLiveThresholdPercent=90
    -XX:G1RSetUpdatingPauseTimePercent=5
    -XX:SurvivorRatio=32
    -XX:+PerfDisableSharedMem
    -XX:MaxTenuringThreshold=1
    -Dusing.aikars.flags=https://mcflags.emc.gs
    -Daikars.new.flags=true
)

echo "[START] Launching Aincrad server..."
echo "[START] Java flags: ${JAVA_FLAGS[*]}"
java "${JAVA_FLAGS[@]}" -jar "${PAPER_JAR}" nogui
