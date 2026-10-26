@echo off
rem Runs the EventHub E2E suites in Docker: Playwright first, then Selenium.
rem Only Docker Desktop is required on this machine. Reports are written to .\reports.
rem
rem Usage:  run-tests.cmd              both suites
rem         run-tests.cmd playwright   one suite
rem         run-tests.cmd selenium
rem
rem The suites run one after the other (never together) because they share one
rem live test account. Selenium still runs if Playwright fails; the script exits
rem with 1 if either suite failed.

setlocal
cd /d "%~dp0"

set "SUITE=%~1"
if "%SUITE%"=="" set "SUITE=all"
if /i not "%SUITE%"=="all" if /i not "%SUITE%"=="playwright" if /i not "%SUITE%"=="selenium" (
  echo Usage: %~nx0 [all^|playwright^|selenium]
  exit /b 2
)

if not exist reports mkdir reports
set "PW_RC=0"
set "SE_RC=0"

if /i not "%SUITE%"=="selenium" (
  echo === Playwright tests ===
  docker compose run --rm --build playwright
  if errorlevel 1 set "PW_RC=1"
)

if /i not "%SUITE%"=="playwright" (
  echo === Selenium tests ===
  docker compose run --rm --build selenium
  if errorlevel 1 set "SE_RC=1"
)

echo.
echo === Summary ===
if /i not "%SUITE%"=="selenium" (
  if "%PW_RC%"=="0" (echo Playwright: PASSED) else (echo Playwright: FAILED)
  echo   report: reports\playwright-report\index.html
)
if /i not "%SUITE%"=="playwright" (
  if "%SE_RC%"=="0" (echo Selenium:   PASSED) else (echo Selenium:   FAILED)
  echo   report: reports\selenium-report\index.html
)

if not "%PW_RC%"=="0" exit /b 1
if not "%SE_RC%"=="0" exit /b 1
exit /b 0
