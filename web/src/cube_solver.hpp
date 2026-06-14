// Thin web-facing wrapper around the existing C++ Rubik's Cube model + solvers.
// No algorithm is reimplemented here: we use RubiksCubeBitboard for the model and
// the project's own solvers (DFS, IDDFS, and IDA* with the corner pattern database).
// This header is shared by the native test harnesses (built with g++) and the
// Emscripten bindings (bindings.cpp).

#ifndef CUBE_SOLVER_HPP
#define CUBE_SOLVER_HPP

#include "bits/stdc++.h"

// The model + database implementation files define everything inline or out-of-line;
// including the .cpp files in a single translation unit is the project's own usage
// pattern (see main.cpp / CornerDBMaker.h).
#include "../../Model/RubiksCube.cpp"
#include "../../Model/RubiksCubeBitboard.cpp"

#include "../../PatternDatabases/math.cpp"
#include "../../PatternDatabases/NibbleArray.cpp"
#include "../../PatternDatabases/PatternDatabase.cpp"
#include "../../PatternDatabases/CornerPatternDatabase.cpp"

#include "../../Solver/DFSSolver.h"
#include "../../Solver/IDDFSSolver.h"
#include "../../Solver/IDAstarSolver.h"

using namespace std;

// Map a move token in standard notation ("U", "R'", "F2", ...) to the MOVE enum.
inline RubiksCube::MOVE parseMove(const string &tok)
{
    static const unordered_map<string, int> table = []()
    {
        unordered_map<string, int> m;
        for (int i = 0; i < 18; i++)
            m[RubiksCube::getMove(RubiksCube::MOVE(i))] = i;
        return m;
    }();
    auto it = table.find(tok);
    return RubiksCube::MOVE(it == table.end() ? 0 : it->second);
}

inline vector<string> splitTokens(const string &s)
{
    vector<string> out;
    string cur;
    stringstream ss(s);
    while (ss >> cur)
        out.push_back(cur);
    return out;
}

inline string movesToString(const vector<RubiksCube::MOVE> &moves)
{
    string out;
    for (size_t i = 0; i < moves.size(); i++)
    {
        if (i)
            out += " ";
        out += RubiksCube::getMove(moves[i]);
    }
    return out;
}

// Stateful cube the frontend drives. The C++ engine is the single source of
// truth for the solution, so the 3D view can never drift out of sync.
class WebCube
{
public:
    RubiksCubeBitboard cube;
    string dbPath; // path to the corner pattern database inside the (virtual) FS

    void reset() { cube = RubiksCubeBitboard(); }

    void applyMoves(const string &tokens)
    {
        for (auto &t : splitTokens(tokens))
            cube.move(parseMove(t));
    }

    void setDbPath(const string &path) { dbPath = path; }

    // 54 color letters (W/G/R/B/O/Y), faces in order U, L, F, R, B, D,
    // each face emitted row-major (row 0..2, col 0..2).
    string state() const
    {
        static const RubiksCube::FACE order[6] = {
            RubiksCube::FACE::UP, RubiksCube::FACE::LEFT, RubiksCube::FACE::FRONT,
            RubiksCube::FACE::RIGHT, RubiksCube::FACE::BACK, RubiksCube::FACE::DOWN};
        string s;
        s.reserve(54);
        for (int f = 0; f < 6; f++)
            for (unsigned r = 0; r < 3; r++)
                for (unsigned c = 0; c < 3; c++)
                    s += RubiksCube::getColorLetter(cube.getColor(order[f], r, c));
        return s;
    }

    bool solved() const { return cube.isSolved(); }

    // Solve the current state. algo is "ida", "iddfs", or "dfs".
    //  - "ida":   optimal, uses the corner pattern database (needs setDbPath),
    //             handles deep scrambles fast. maxDepth is ignored.
    //  - "iddfs": optimal via iterative-deepening DFS, no database (shallow only).
    //  - "dfs":   bounded depth-first search, no database.
    // Returns a space-separated token string; does NOT mutate this cube.
    string solve(const string &algo, int maxDepth)
    {
        // Returns "ERROR" if the solver runs out of memory or throws — the deep
        // IDA* hybrid keeps a visited set, so very deep scrambles can exhaust the
        // heap. The UI caps the scramble depth to stay well inside the safe zone.
        try
        {
            if (algo == "ida")
            {
                IDAstarSolver<RubiksCubeBitboard, HashBitboard> solver(cube, dbPath);
                return movesToString(solver.solve());
            }
            if (algo == "dfs")
            {
                DFSSolver<RubiksCubeBitboard, HashBitboard> solver(cube, maxDepth);
                return movesToString(solver.solve());
            }
            IDDFSSolver<RubiksCubeBitboard, HashBitboard> solver(cube, maxDepth);
            return movesToString(solver.solve());
        }
        catch (...)
        {
            return "ERROR";
        }
    }
};

#endif // CUBE_SOLVER_HPP
