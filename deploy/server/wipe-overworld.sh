#!/bin/sh
# Wipes only the vanilla overworld dimension, keeping custom Aincrad floors and
# all player/plugin data. Run as: sh wipe-overworld.sh
set -e
cd "$(dirname "$0")"
export PATH="$PATH:/mnt/c/Program Files/Docker/Docker/resources/bin"
ts=$(date +%Y%m%d_%H%M%S)
mkdir -p data_backups
docker.exe compose down
tar czf "data_backups/overworld_$ts.tar.gz" \
  world/dimensions/minecraft/overworld world/level.dat world/level.dat_old 2>/dev/null || true
cmd.exe /c "rd /s /q G:\\BigProjects\\Aincrad\\deploy\\server\\world\\dimensions\\minecraft\\overworld"
cmd.exe /c "del /q G:\\BigProjects\\Aincrad\\deploy\\server\\world\\level.dat G:\\BigProjects\\Aincrad\\deploy\\server\\world\\level.dat_old"
sleep 2
[ -d world/dimensions/minecraft/overworld ] && { echo "WIPE FAILED: overworld still present"; exit 1; }
docker.exe compose up -d
