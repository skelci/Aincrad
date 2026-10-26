#!/bin/sh
# Snapshot plugin persistence before any restart/wipe. Run as: sh backup-data.sh
set -e
cd "$(dirname "$0")"
ts=$(date +%Y%m%d_%H%M%S)
mkdir -p data_backups
tar czf "data_backups/aincrad-data_$ts.tar.gz" data/Aincrad data/players 2>/dev/null || \
  tar czf "data_backups/aincrad-data_$ts.tar.gz" data/Aincrad
ls -t data_backups | head -3
# keep last 20
ls -t data_backups | tail -n +21 | xargs -I{} rm -f "data_backups/{}"
