@echo off
setlocal

set GRADLE_VERSION=8.7
set APP_HOME=%~dp0
set WRAPPER_DIR=%APP_HOME%.gradle-wrapper-local
set ZIP_PATH=%WRAPPER_DIR%\gradle-%GRADLE_VERSION%-bin.zip
set DIST_DIR=%WRAPPER_DIR%\gradle-%GRADLE_VERSION%
set GRADLE_BIN=%DIST_DIR%\bin\gradle.bat
set DIST_URL=https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip

if not exist "%WRAPPER_DIR%" mkdir "%WRAPPER_DIR%"

if not exist "%GRADLE_BIN%" (
  echo Downloading Gradle %GRADLE_VERSION%...
  powershell -Command "Invoke-WebRequest -Uri '%DIST_URL%' -OutFile '%ZIP_PATH%'"
  powershell -Command "Expand-Archive -Path '%ZIP_PATH%' -DestinationPath '%WRAPPER_DIR%' -Force"
)

call "%GRADLE_BIN%" -p "%APP_HOME%" %*
