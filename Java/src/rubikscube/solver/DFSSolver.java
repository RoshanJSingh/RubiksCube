package rubikscube.solver;

import rubikscube.model.RubiksCube;
import java.util.ArrayList;
import java.util.List;

public class DFSSolver<T extends RubiksCube> {

    private final T rubiksCube;
    private final int maxSearchDepth;
    private final List<RubiksCube.MOVE> moves;

    public DFSSolver(T rubiksCube, int maxSearchDepth) {
        this.rubiksCube = rubiksCube;
        this.maxSearchDepth = maxSearchDepth;
        this.moves = new ArrayList<>();
    }

    public List<RubiksCube.MOVE> solve() {
        dfs(1);
        return moves;
    }

    private boolean dfs(int dep) {
        if (rubiksCube.isSolved())
            return true;
        if (dep > maxSearchDepth)
            return false;

        for (RubiksCube.MOVE move : RubiksCube.MOVE.values()) {
            rubiksCube.move(move);
            moves.add(move);
            if (dfs(dep + 1))
                return true;
            moves.remove(moves.size() - 1);
            rubiksCube.invert(move);
        }
        return false;
    }
}
