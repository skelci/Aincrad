#!/bin/sh
# World-only wipe. Player data lives in data/players (bukkit.yml
# player-data-folder) and plugin state in data/Aincrad - both survive.
set -e
cd "$(dirname "$0")"
export PATH="$PATH:/mnt/c/Program Files/Docker/Docker/resources/bin"
docker.exe compose down
cmd.exe /c "rd /s /q world world_nether world_the_end"
sleep 2
[ -d world ] && { echo "WIPE FAILED: world still present"; exit 1; }
docker.exe compose up -d
