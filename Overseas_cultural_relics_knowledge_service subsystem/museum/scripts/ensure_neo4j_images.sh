#!/usr/bin/env bash
# 定时同步 MySQL 图片到 Neo4j，防止图谱缺图导致占位图。
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
export NEO4J_URI="${NEO4J_URI:-bolt://39.106.231.119:7687}"
export NEO4J_PASSWORD="${NEO4J_PASSWORD:-password123}"
LOG="${NEO4J_IMAGE_SYNC_LOG:-/tmp/neo4j-image-sync.log}"
echo "[$(date -Iseconds)] start neo4j image sync" >> "$LOG"
"$ROOT/.venv-sync/bin/python3" sync_mysql_to_neo4j.py --allow-remote >> "$LOG" 2>&1
echo "[$(date -Iseconds)] done" >> "$LOG"
