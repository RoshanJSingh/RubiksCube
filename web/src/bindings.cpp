// Emscripten/embind bindings that expose WebCube to JavaScript.
// Compiled to WASM (see web/build.sh / the GitHub Actions workflow).

#include "cube_solver.hpp"
#include <emscripten/bind.h>

using namespace emscripten;

EMSCRIPTEN_BINDINGS(rubiks_cube_module)
{
    class_<WebCube>("WebCube")
        .constructor<>()
        .function("reset", &WebCube::reset)
        .function("applyMoves", &WebCube::applyMoves)
        .function("state", &WebCube::state)
        .function("solved", &WebCube::solved)
        .function("solve", &WebCube::solve);
}
