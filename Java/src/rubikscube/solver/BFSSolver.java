package rubikscube.solver;

import rubikscube.model.RubiksCube;
import java.util.*;

public class BFSSolver<T extends RubiksCube> {

    private final T rubiksCube;
    private final Map<T, T> parents;
    private final Map<T, RubiksCube.MOVE> moveFromParent;

    public BFSSolver(T rubiksCube) {
        this.rubiksCube = rubiksCube;
        this.parents = new HashMap<>();
        this.moveFromParent = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public List<RubiksCube.MOVE> solve() {
        Queue<T> q = new ArrayDeque<>();
        q.add(rubiksCube);
        parents.put(rubiksCube, null);

        while (!q.isEmpty()) {
            T node = q.poll();

            if (node.isSolved()) {
                return reconstructPath(node);
            }

            for (RubiksCube.MOVE move : RubiksCube.MOVE.values()) {
                T nextNode = (T) node.copy();
                nextNode.move(move);

                if (!parents.containsKey(nextNode)) {
                    parents.put(nextNode, node);
                    moveFromParent.put(nextNode, move);
                    q.add(nextNode);
                }
            }
        }
        return new ArrayList<>();
    }

    private List<RubiksCube.MOVE> reconstructPath(T endNode) {
        List<RubiksCube.MOVE> path = new ArrayList<>();
        T curr = endNode;
        while (curr != null) {
            RubiksCube.MOVE move = moveFromParent.get(curr);
            if (move != null) {
                path.add(move);
            }
            curr = parents.get(curr);
        }
        Collections.reverse(path);
        return path;
    }
}
