# Rubik's Cube Solver in the browser (C++ compiled to WebAssembly)

This folder is a 3D web front end for the C++ solver in this repo. The solving is done by the C++ engine itself, compiled to WebAssembly with Emscripten. It runs entirely in the visitor's browser, so there is no server involved. The front end, built with Three.js, only draws the cube and animates the moves the engine sends back.

```
web/
├── public/                  the static site that gets deployed
│   ├── index.html
│   ├── style.css
│   ├── app.js               Three.js cube, animation and UI (main thread)
│   ├── worker.js            Web Worker that runs the WASM engine and loads the database
│   ├── cornerDepth5V1.txt   corner pattern database (about 48 MB, used by IDA*)
│   └── rubiks.mjs / rubiks.wasm   created by build.sh, not committed
├── src/
│   ├── cube_solver.hpp      WebCube, a wrapper around the existing model and solvers
│   ├── bindings.cpp         Emscripten (embind) bindings
│   ├── test_native.cpp      native g++ test for DFS and IDDFS (correctness and timing)
│   ├── test_ida.cpp         native g++ test for IDA* using the real database
│   └── shims/bits/stdc++.h  stand-in for <bits/stdc++.h>, which Emscripten does not have
└── build.sh                 the emcc build command
```

## How it works

- `WebCube` in `src/cube_solver.hpp` wraps `RubiksCubeBitboard` and the solvers. It gives JavaScript `reset()`, `applyMoves("U R' F2 ...")`, `setDbPath()`, `state()`, `solved()` and `solve(algo, maxDepth)`.
- The browser makes a random scramble. The worker passes it to the C++ engine, gets a solution back, and the 3D cube plays it move by move.
- You can pick one of three algorithms:
  - **IDA\*** finds the shortest solution using the corner pattern database and can handle deep scrambles. The 48 MB database is loaded into the worker's in-memory file system the first time you use IDA\*. This version of IDA\* keeps a visited set, so the page limits scrambles to 12 moves. It runs out of memory at around 14, which is also why the original `main.cpp` used depth 13.
  - **IDDFS** finds the shortest solution by iterative deepening without a database. Limited to 6 moves.
  - **DFS** returns the first solution it finds within the depth limit, without a database. Limited to 6 moves.
- Solving happens in a Web Worker, so the cube stays responsive even when IDA\* takes a few seconds.

## Building locally (optional)

You only need this to preview the WASM build on your own machine. The GitHub workflow builds it automatically.

1. Install the [Emscripten SDK](https://emscripten.org/docs/getting_started/downloads.html) so that `emcc` is on your `PATH`.
2. From the repo root, run:
   ```bash
   bash web/build.sh
   ```
   This creates `web/public/rubiks.mjs` and `web/public/rubiks.wasm`.
3. Serve the folder over HTTP, because WASM will not load from `file://`:
   ```bash
   python -m http.server -d web/public 8080
   ```
   Then open <http://localhost:8080>.

### Testing the C++ side without Emscripten

```bash
# DFS and IDDFS:
g++ -std=c++14 -O2 -o web/src/test_native web/src/test_native.cpp
./web/src/test_native      # prints solve times and checks each solution

# IDA* with the real database:
g++ -std=c++14 -O2 -o web/src/test_ida web/src/test_ida.cpp
./web/src/test_ida "path/to/cornerDepth5V1.txt"
```

## Deploying to GitHub Pages

`.github/workflows/deploy.yml` builds the WASM and publishes `web/public/` every time something is pushed to `main`.

1. In the repo, go to **Settings**, then **Pages**, then **Build and deployment**, and set the source to **GitHub Actions**.
2. Push to `main`, or run the workflow by hand from the **Actions** tab.
3. The site is published at `https://roshanjsingh.github.io/RubiksCube/`.

No secrets or paid services are needed.
