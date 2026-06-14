#!/usr/bin/env bash
# Compile the C++ solver to WebAssembly. Requires the Emscripten SDK (emcc) on PATH.
# Output lands in web/public/ next to the frontend so it can be deployed as a static site.
set -euo pipefail
cd "$(dirname "$0")"

mkdir -p public

emcc src/bindings.cpp \
  -I src/shims \
  -std=c++14 -O3 \
  -lembind \
  -s MODULARIZE=1 \
  -s EXPORT_ES6=1 \
  -s EXPORT_NAME=createRubiksModule \
  -s ENVIRONMENT=web \
  -s ALLOW_MEMORY_GROWTH=1 \
  -s INITIAL_MEMORY=33554432 \
  -o public/rubiks.mjs

echo "Built: web/public/rubiks.mjs + web/public/rubiks.wasm"
