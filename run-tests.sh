#!/usr/bin/env bash
# Runs the EventHub E2E suites in Docker: Playwright first, then Selenium.
# Only Docker is required on this machine. Reports are written to ./reports.
#
# Usage:  ./run-tests.sh              # both suites
#         ./run-tests.sh playwright   # one suite
#         ./run-tests.sh selenium
#
# The suites run one after the other (never together) because they share one
# live test account. Selenium still runs if Playwright fails; the script exits
# non-zero if either suite failed.

set -u
cd "$(dirname "$0")"

suite="${1:-all}"
case "$suite" in
  all|playwright|selenium) ;;
  *) echo "Usage: $0 [all|playwright|selenium]" >&2; exit 2 ;;
esac

mkdir -p reports
playwright_rc=0
selenium_rc=0

if [ "$suite" = "all" ] || [ "$suite" = "playwright" ]; then
  echo "=== Playwright tests ==="
  docker compose run --rm --build playwright || playwright_rc=$?
fi

if [ "$suite" = "all" ] || [ "$suite" = "selenium" ]; then
  echo "=== Selenium tests ==="
  docker compose run --rm --build selenium || selenium_rc=$?
fi

echo
echo "=== Summary ==="
if [ "$suite" != "selenium" ]; then
  [ "$playwright_rc" -eq 0 ] && echo "Playwright: PASSED" || echo "Playwright: FAILED (exit $playwright_rc)"
  echo "  report: reports/playwright-report/index.html"
fi
if [ "$suite" != "playwright" ]; then
  [ "$selenium_rc" -eq 0 ] && echo "Selenium:   PASSED" || echo "Selenium:   FAILED (exit $selenium_rc)"
  echo "  report: reports/selenium-report/index.html"
fi

[ "$playwright_rc" -eq 0 ] && [ "$selenium_rc" -eq 0 ]
