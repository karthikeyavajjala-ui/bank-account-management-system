#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
javac -encoding UTF-8 *.java
exec java Main
