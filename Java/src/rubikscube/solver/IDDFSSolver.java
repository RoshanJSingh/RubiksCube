package rubikscube.solver;

import rubikscube.model.RubiksCube;
import java.util.List;
import java.util.ArrayList;

public class IDDFSSolver<T extends RubiksCube> {

    private final int maxSearchDepth;
    private T rubiksCube;
    private List<RubiksCube.MOVE> moves;

    public IDDFSSolver(T rubiksCube, int maxSearchDepth) {
        this.rubiksCube = rubiksCube;
        this.maxSearchDepth = maxSearchDepth;
        this.moves = new ArrayList<>();
    }

    public List<RubiksCube.MOVE> solve() {
        for (int i = 1; i <= maxSearchDepth; i++) {
            DFSSolver<T> dfsSolver = new DFSSolver<>(rubiksCube, i);
            moves = dfsSolver.solve();
            // We need to check if it's solved. DFSSolver modifies the cube in place?
            // Wait, DFSSolver in C++ modifies the cube and backtracks.
            // But if it finds a solution, it returns true and the cube is in solved state?
            // No, DFSSolver in C++:
            // if (dfs(dep + 1)) return true;
            // moves.pop_back();
            // rubiksCube.invert(move);

            // If it returns true, it does NOT backtrack the last move?
            // Actually, if `dfs` returns true, it returns true immediately up the stack.
            // So the cube is left in the solved state (or partially solved state if we
            // consider the path).
            // But `DFSSolver` in Java I implemented:
            /*
             * if (dfs(dep + 1)) return true;
             * moves.remove(moves.size() - 1);
             * rubiksCube.invert(move);
             */
            // If `dfs` returns true, it returns true. It does NOT execute the backtrack
            // lines.
            // So the cube IS modified to the solved state.

            // However, `IDDFSSolver` needs to check if it was solved.
            // `DFSSolver.solve()` returns the moves.
            // If moves is not empty (or if we check isSolved), we are done.
            // But `DFSSolver` might return empty list if not found?
            // My `DFSSolver` returns `moves` which is populated if found.
            // But if not found, it returns empty list?
            // Wait, `dfs` returns boolean. `solve` calls `dfs(1)`.
            // If `dfs(1)` returns false, `moves` will be empty (backtracked).
            // If `dfs(1)` returns true, `moves` will contain the solution.

            // So if `moves` is not empty, we found it?
            // What if the cube is already solved? `moves` is empty.
            // So we should check `rubiksCube.isSolved()`.

            // But `DFSSolver` modifies `this.rubiksCube`.
            // In `IDDFSSolver`, we pass `rubiksCube`.
            // So `rubiksCube` is modified.

            if (rubiksCube.isSolved()) {
                break;
            }
        }
        return moves;
    }
}
