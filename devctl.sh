#!/usr/bin/env bash
# devctl - developer control script for the Flight Booking API (macOS / Linux).
# One entry point for common dev tasks: start / stop / restart the server, build,
# run tests, drive Docker, and view logs.  Run './devctl.sh help' for usage.

set -euo pipefail
cd "$(dirname "$0")"

# --- Configuration -------------------------------------------------------
APP_NAME='flight-booking'
PORT=8081
DEFAULT_PROFILE='dev'
BASE_URL="http://localhost:${PORT}"
LOG_DIR='logs'
APP_LOG="${LOG_DIR}/flight-booking.log"
CONSOLE_LOG="${LOG_DIR}/devctl-console.log"
STATE_DIR='.devctl'
PID_FILE="${STATE_DIR}/server.pid"
GRADLEW='./gradlew'
DOCKER_IMAGE='flight-booking'
DOCKER_NAME='flight-booking'
START_TIMEOUT=90

# --- Helpers -------------------------------------------------------------
server_pid() {
  if command -v lsof >/dev/null 2>&1; then
    lsof -ti "tcp:${PORT}" -sTCP:LISTEN 2>/dev/null | head -n1 || true
  elif command -v ss >/dev/null 2>&1; then
    ss -ltnp 2>/dev/null | grep ":${PORT} " | grep -oE 'pid=[0-9]+' | head -n1 | cut -d= -f2 || true
  fi
}

is_running() { [ -n "$(server_pid)" ]; }

cmd_start() {
  local profile="${1:-$DEFAULT_PROFILE}"
  if is_running; then
    echo "Already running on ${BASE_URL} (PID $(server_pid))."
    return 0
  fi
  mkdir -p "$LOG_DIR" "$STATE_DIR"
  echo "Starting ${APP_NAME} (profile: ${profile}) ..."
  nohup "$GRADLEW" bootRun "--args=--spring.profiles.active=${profile}" >"$CONSOLE_LOG" 2>&1 &
  echo "$!" >"$PID_FILE"
  printf 'Waiting for %s ' "$BASE_URL"
  local t=0
  while [ "$t" -lt "$START_TIMEOUT" ]; do
    if is_running; then
      printf '\nStarted (PID %s).  Swagger: %s/swagger-ui.html\n' "$(server_pid)" "$BASE_URL"
      return 0
    fi
    sleep 1
    printf '.'
    t=$((t + 1))
  done
  printf '\nDid not open port %s within %ss. See %s.\n' "$PORT" "$START_TIMEOUT" "$CONSOLE_LOG"
  return 1
}

cmd_stop() {
  local pid
  pid="$(server_pid)"
  if [ -n "$pid" ]; then
    echo "Stopping server (PID ${pid}) ..."
    kill "$pid" 2>/dev/null || true
    echo "Stopped."
  else
    echo "Not running (nothing on port ${PORT})."
  fi
  if [ -f "$PID_FILE" ]; then
    kill "$(cat "$PID_FILE")" 2>/dev/null || true
    rm -f "$PID_FILE"
  fi
}

cmd_status() {
  local pid
  pid="$(server_pid)"
  if [ -n "$pid" ]; then
    echo "RUNNING  ${BASE_URL}  (PID ${pid})"
  else
    echo "STOPPED  (nothing listening on port ${PORT})"
  fi
}

cmd_logs() {
  local lines=50 since='' follow='' lines_explicit=''
  while [ "$#" -gt 0 ]; do
    case "$1" in
      -n|--lines)  lines="$2"; lines_explicit=1; shift 2 ;;
      -s|--since)  since="$2"; shift 2 ;;
      -f|--follow) follow=1; shift ;;
      *) shift ;;
    esac
  done
  if [ ! -f "$APP_LOG" ]; then
    echo "No log yet at ${APP_LOG}. Start the server first (./devctl.sh start)."
    return 0
  fi
  if [ -n "$since" ]; then
    # Log lines start with 'yyyy-MM-dd HH:mm:ss.SSS', which sorts lexicographically by time.
    awk -v since="$since" '
      !printing {
        ts = substr($0, 1, length(since))
        if (ts >= since) printing = 1
      }
      printing { print }
    ' "$APP_LOG"
    return 0
  fi
  if [ -n "$follow" ] || [ -z "$lines_explicit" ]; then
    tail -n "$lines" -f "$APP_LOG"
  else
    tail -n "$lines" "$APP_LOG"
  fi
}

open_url() {
  if command -v xdg-open >/dev/null 2>&1; then xdg-open "$1" >/dev/null 2>&1 &
  elif command -v open >/dev/null 2>&1; then open "$1"
  else echo "Open manually: $1"; fi
}

usage() {
  cat <<'EOF'
devctl - developer control for the Flight Booking API

Usage: ./devctl.sh <command> [options]

Commands (long | short):
  start        | up     Start the server in the background (profile: dev)
  stop         | down   Stop the running server
  restart      | re     Restart the server
  status       | st     Show whether the server is running
  build        | b      Clean-compile and package the boot jar (skips tests)
  test         | t      Run the test suite
  zip          | z      Package sources/docs into dist/ (archiver)
  docker-build | db     Build the Docker image
  docker-run   | dr     Run the Docker image (detached) on the app port
  docker-stop  | dx     Stop the Docker container
  docker-logs  | dl     Follow the Docker container logs
  logs         | l      View / follow the application log
  open         | o      Open the Swagger UI in the default browser
  help         | h      Show this help

Options:
  -p, --profile <name>  Spring profile for start/restart (default: dev)
  -n, --lines   <N>     logs: show the last N lines (no follow)
  -s, --since   <ts>    logs: show lines from 'yyyy-MM-dd HH:mm:ss[.fff]'
  -f, --follow          logs: follow live output

Examples:
  ./devctl.sh start
  ./devctl.sh up -p prod
  ./devctl.sh re
  ./devctl.sh logs -f
  ./devctl.sh l -n 200
  ./devctl.sh logs --since "2026-07-08 12:00:00"
  ./devctl.sh db && ./devctl.sh dr
EOF
}

# --- Dispatch (long + short aliases) ------------------------------------
cmd="${1:-help}"
shift || true

profile="$DEFAULT_PROFILE"
parse_profile() {
  while [ "$#" -gt 0 ]; do
    case "$1" in
      -p|--profile) profile="$2"; shift 2 ;;
      *) shift ;;
    esac
  done
}

case "$cmd" in
  start|up)         parse_profile "$@"; cmd_start "$profile" ;;
  stop|down)        cmd_stop ;;
  restart|re)       parse_profile "$@"; cmd_stop; cmd_start "$profile" ;;
  status|st)        cmd_status ;;
  build|b)          "$GRADLEW" clean bootJar ;;
  test|t)           "$GRADLEW" test ;;
  zip|archive|z)    "$GRADLEW" runArchiver ;;
  docker-build|db)  docker build -t "$DOCKER_IMAGE" . ;;
  docker-run|dr)    docker run -d --rm -p "${PORT}:${PORT}" --name "$DOCKER_NAME" "$DOCKER_IMAGE" \
                      && echo "Container '${DOCKER_NAME}' on ${BASE_URL}. Logs: ./devctl.sh docker-logs" ;;
  docker-stop|dx)   docker stop "$DOCKER_NAME" ;;
  docker-logs|dl)   docker logs -f "$DOCKER_NAME" ;;
  logs|log|l)       cmd_logs "$@" ;;
  open|o)           open_url "${BASE_URL}/swagger-ui.html" ;;
  help|h|-h|--help) usage ;;
  *) echo "Unknown command: '$cmd'"; echo; usage; exit 1 ;;
esac
