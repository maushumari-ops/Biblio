#!/usr/bin/env sh
set -eu

GRADLE_VERSION="8.7"
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
WRAPPER_DIR="$APP_HOME/.gradle-wrapper-local"
DIST_DIR="$WRAPPER_DIR/gradle-$GRADLE_VERSION"
ZIP_PATH="$WRAPPER_DIR/gradle-$GRADLE_VERSION-bin.zip"
GRADLE_BIN="$DIST_DIR/bin/gradle"
DIST_URL="https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"

mkdir -p "$WRAPPER_DIR"

if [ ! -x "$GRADLE_BIN" ]; then
  echo "Downloading Gradle $GRADLE_VERSION..."
  if command -v curl >/dev/null 2>&1; then
    curl -L --fail --retry 3 -o "$ZIP_PATH" "$DIST_URL"
  elif command -v wget >/dev/null 2>&1; then
    wget -O "$ZIP_PATH" "$DIST_URL"
  else
    echo "curl or wget is required to download Gradle." >&2
    exit 1
  fi

  rm -rf "$DIST_DIR"
  unzip -q "$ZIP_PATH" -d "$WRAPPER_DIR"
fi

exec "$GRADLE_BIN" -p "$APP_HOME" "$@"
