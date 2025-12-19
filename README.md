
Rubik's Cube Solver
This project is a Rubik's Cube solver implemented in C++. It provides multiple representations of the Rubik's Cube (3D array, 1D array, and bitboard) and uses various solving algorithms such as Depth-First Search (DFS), Breadth-First Search (BFS), Iterative Deepening Depth-First Search (IDDFS), and IDA*.

Features
Cube Representations:
RubiksCube3dArray: A 3D array representation of the cube.
RubiksCube1dArray: A 1D array representation of the cube.
RubiksCubeBitboard: A bitboard representation for efficient operations.
Solving Algorithms:
DFS Solver: Depth-First Search with a maximum depth limit.
BFS Solver: Breadth-First Search for finding the shortest solution.
IDDFS Solver: Iterative Deepening Depth-First Search.
IDA Solver*: A heuristic-based solver using pattern databases.
Pattern Databases:
Corner Pattern Database for heuristic calculations.

Prerequisites
Before running the project, ensure you have the following installed:
C++ Compiler:
GCC (MinGW for Windows) or Clang.
CMake:
Install CMake from https://cmake.org/download/.
OpenMP:
Ensure your compiler supports OpenMP for parallel processing.

Project Structure
Here is a detailed README.md file for your project:
# Rubik's Cube Solver

This project is a Rubik's Cube solver implemented in C++. It provides multiple representations of the Rubik's Cube (3D array, 1D array, and bitboard) and uses various solving algorithms such as Depth-First Search (DFS), Breadth-First Search (BFS), Iterative Deepening Depth-First Search (IDDFS), and IDA*.

---

## Features

- **Cube Representations**:
  - `RubiksCube3dArray`: A 3D array representation of the cube.
  - `RubiksCube1dArray`: A 1D array representation of the cube.
  - `RubiksCubeBitboard`: A bitboard representation for efficient operations.

- **Solving Algorithms**:
  - **DFS Solver**: Depth-First Search with a maximum depth limit.
  - **BFS Solver**: Breadth-First Search for finding the shortest solution.
  - **IDDFS Solver**: Iterative Deepening Depth-First Search.
  - **IDA* Solver**: A heuristic-based solver using pattern databases.

- **Pattern Databases**:
  - Corner Pattern Database for heuristic calculations.

---

## Prerequisites

Before running the project, ensure you have the following installed:

1. **C++ Compiler**:
   - GCC (MinGW for Windows) or Clang.
2. **CMake**:
   - Install CMake from [https://cmake.org/download/](https://cmake.org/download/).
3. **OpenMP**:
   - Ensure your compiler supports OpenMP for parallel processing.

---

## Project Structure


Rubiks_Cube/ ├── Model/ │ ├── RubiksCube.h │ ├── RubiksCube.cpp │ ├── RubiksCube3dArray.cpp │ ├── RubiksCube1dArray.cpp │ ├── RubiksCubeBitboard.cpp ├── Solver/ │ ├── DFSSolver.h │ ├── BFSSolver.h │ ├── IDDFSSolver.h │ ├── IDAstarSolver.h ├── PatternDatabases/ │ ├── CornerPatternDatabase.cpp │ ├── CornerPatternDatabase.h │ ├── CornerDBMaker.cpp │ ├── CornerDBMaker.h │ ├── NibbleArray.cpp │ ├── NibbleArray.h │ ├── math.cpp │ ├── math.h ├── Databases/ │ ├── cornerDepth5V1.txt ├── main.cpp ├── CMakeLists.txt └── README.md

---

## How to Build and Run

### 1. **Clone the Repository**
```bash
git clone https://github.com/RoshanJSingh/Rubiks-Cube.git
cd Rubiks-Cube


2. Build the Project
Using CMake
Create a build directory:
mkdir build
cd build
Run CMake to configure the project:
cmake ..
Build the project:
cmake --build .
Using g++
Alternatively, you can compile the project directly using g++:
g++ main.cpp RubiksCube3dArray.cpp RubiksCube1dArray.cpp RubiksCubeBitboard.cpp CornerPatternDatabase.cpp CornerDBMaker.cpp -o rubiks_cube_solver -fopenmp
3. Run the Executable
After building, run the executable:
./rubiks_cube_solver

Usage
Example Code
The main.cpp file contains example usage of the solvers. You can uncomment sections to test different solvers or representations.
Example Output
The program will:
Randomly shuffle the cube.
Solve the cube using the selected algorithm.
Print the solution moves and the solved cube.

Contributing
Contributions are welcome! If you find any bugs or have suggestions for improvements, feel free to open an issue or submit a pull request.

License
This project is licensed under the MIT License. See the LICENSE file for details.

Author
Roshan J Singh - Project Creator


