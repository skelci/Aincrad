#!/usr/bin/env bash
# Aincrad Server Stop Script
# Gracefully stops the server with save
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PID_FILE="${SCRIPT_DIR}/server.pid"

if [[ ! -f "${PID_FILE}" ]]; then
    echo "[STOP] No PID file found. Server may not be running."
    exit 0
fi

PID=$(cat "${PID_FILE}")
if ! kill -0 "${PID}" 2>/dev/null; then
    echo "[STOP] Server process ${PID} not running."
    rm -f "${PID_FILE}"
    exit 0
fi

echo "[STOP] Sending stop signal to server (PID: ${PID})..."
kill -TERM "${PID}"

# Wait up to 60 seconds for graceful shutdown
for i in $(seq 1 60); do
    if ! kill -0 "${PID}" 2>/dev/null; then
        echo "[STOP] Server stopped gracefully."
        rm -f "${PID_FILE}"
        exit 0
    fi
    sleep 1
done

echo "[STOP] Server did not stop in time. Sending SIGKILL..."
kill -KILL "${PID}" 2>/dev/null || true
rm -f "${PID_FILE}"
echo "[STOP] Server killed."
