# Rubik's Cube Solver

A Rubik's Cube solver written in C++. It models the cube in three different ways and solves it with several search algorithms, from plain DFS and BFS up to IDA* with a pattern database.

You can try it in the browser here: [roshanjsingh.github.io/RubiksCube](https://roshanjsingh.github.io/RubiksCube/). The web version runs the same C++ code, compiled to WebAssembly. See [web/README.md](web/README.md) for how that works.

## What is inside

**Three ways to represent the cube**

- `RubiksCube3dArray`: the cube as a 3D array of faces
- `RubiksCube1dArray`: the same data flattened into a 1D array
- `RubiksCubeBitboard`: a bitboard version, which is the fastest to copy and compare

**Solvers**

- DFS: depth-first search with a depth limit
- BFS: breadth-first search, which finds the shortest solution but uses a lot of memory
- IDDFS: iterative deepening, which also finds the shortest solution with far less memory
- IDA*: iterative deepening with a heuristic, which can handle much deeper scrambles

**Pattern database**

IDA* gets its heuristic from a corner pattern database. It stores how many moves each corner arrangement needs to be solved, packed into a nibble array to save space.

## Project layout

```
RubiksCube/
├── Model/              the three cube representations
├── Solver/             DFS, BFS, IDDFS and IDA* solvers
├── PatternDatabases/   corner pattern database, nibble array, permutation indexer
├── web/                browser version (C++ compiled to WebAssembly)
├── main.cpp            example runs of the solvers
└── CMakeLists.txt
```

## Building

You need a C++ compiler (GCC, MinGW on Windows, or Clang) and CMake 3.20 or newer.

```bash
git clone https://github.com/RoshanJSingh/RubiksCube.git
cd RubiksCube
mkdir build
cd build
cmake ..
cmake --build .
./rubiks_cube_solver
```

## Using it

`main.cpp` has example runs for each solver and each cube type. Uncomment the part you want to try. The program scrambles the cube at random, solves it with the chosen algorithm, and prints the moves along with the solved cube.
