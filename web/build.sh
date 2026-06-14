#!/usr/bin/env bash
# Compile the C++ solver to WebAssembly. Requires the Emscripten SDK (emcc) on PATH.
# Output lands in web/public/ next to the frontend so it can be deployed as a static site.
set -euo pipefail
cd "$(dirname "$0")"

mkdir -p public

emcc src/bindings.cpp \
  -I src/shims \
  -std=c++14 -O3 -fexceptions \
  -lembind \
  -s MODULARIZE=1 \
  -s EXPORT_ES6=1 \
  -s EXPORT_NAME=createRubiksModule \
  -s ENVIRONMENT=web,worker \
  -s FORCE_FILESYSTEM=1 \
  -s EXPORTED_RUNTIME_METHODS=FS \
  -s ALLOW_MEMORY_GROWTH=1 \
  -s INITIAL_MEMORY=268435456 \
  -s MAXIMUM_MEMORY=2147483648 \
  -o public/rubiks.mjs

echo "Built: web/public/rubiks.mjs + web/public/rubiks.wasm"
