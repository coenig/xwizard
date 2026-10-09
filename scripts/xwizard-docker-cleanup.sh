#!/usr/bin/env bash
# Weekly housekeeping: keep build cache/images/logs from growing unbounded
# on this memory/disk-constrained host (see MODERNIZATION-HANDOFF.md incident notes).
set -euo pipefail
LOG=/var/log/xwizard-docker-cleanup.log
{
	echo "=== $(date -u +%FT%TZ) ==="
	docker builder prune -af --filter 'until=168h'
	docker image prune -af --filter 'until=168h'
	journalctl --vacuum-time=14d
} >>"$LOG" 2>&1
