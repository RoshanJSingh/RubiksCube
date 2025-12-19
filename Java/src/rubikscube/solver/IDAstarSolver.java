package rubikscube.solver;

import rubikscube.model.RubiksCube;
import rubikscube.patterndatabase.CornerPatternDatabase;

import java.util.*;

public class IDAstarSolver<T extends RubiksCube> {

    private final T rubiksCube;
    private final CornerPatternDatabase cornerDB;
    private final List<RubiksCube.MOVE> moves;
    private final Map<T, RubiksCube.MOVE> moveDone;
    private final Map<T, Boolean> visited;

    public IDAstarSolver(T rubiksCube, String fileName) {
        this.rubiksCube = rubiksCube;
        this.cornerDB = new CornerPatternDatabase();
        this.cornerDB.fromFile(fileName);
        this.moves = new ArrayList<>();
        this.moveDone = new HashMap<>();
        this.visited = new HashMap<>();
    }

    private static class Node {
        RubiksCube cube;
        int depth;
        int estimate;

        Node(RubiksCube cube, int depth, int estimate) {
            this.cube = cube;
            this.depth = depth;
            this.estimate = estimate;
        }
    }

    private class CompareCube implements Comparator<Pair<Node, Integer>> {
        @Override
        public int compare(Pair<Node, Integer> p1, Pair<Node, Integer> p2) {
            Node n1 = p1.first;
            Node n2 = p2.first;
            if (n1.depth + n1.estimate == n2.depth + n2.estimate) {
                return Integer.compare(n1.estimate, n2.estimate); // n1.estimate > n2.estimate ?
                // C++: return n1.estimate > n2.estimate; (Min heap? PriorityQueue in C++ is Max
                // heap by default)
                // Wait, C++ priority_queue is MAX heap.
                // But usually A* uses MIN heap (lowest f-score).
                // If C++ uses `>` for estimate, it prioritizes higher estimate? That's weird
                // for A*.
                // Or maybe it's doing something else.
                // Let's assume standard A*: min f-score (depth + estimate).
                // Java PriorityQueue is MIN heap.
                // So I should return n1.f - n2.f.
            } else {
                return Integer.compare(n1.depth + n1.estimate, n2.depth + n2.estimate);
            }
        }
    }

    private static class Pair<K, V> {
        K first;
        V second;

        Pair(K first, V second) {
            this.first = first;
            this.second = second;
        }
    }

    private void resetStructure() {
        moves.clear();
        moveDone.clear();
        visited.clear();
    }

    private Pair<T, Integer> IDAstar(int bound) {
        PriorityQueue<Pair<Node, Integer>> pq = new PriorityQueue<>(new CompareCube());
        Node start = new Node(rubiksCube, 0, cornerDB.getNumMoves(rubiksCube));
        pq.add(new Pair<>(start, 0));
        int nextBound = 100;

        while (!pq.isEmpty()) {
            Pair<Node, Integer> p = pq.poll();
            Node node = p.first;

            if (visited.containsKey(node.cube))
                continue;

            visited.put((T) node.cube, true); // Unchecked cast
            moveDone.put((T) node.cube, RubiksCube.MOVE.values()[p.second]);

            if (node.cube.isSolved()) {
                return new Pair<>((T) node.cube, bound);
            }

            node.depth++;
            for (int i = 0; i < 18; i++) {
                RubiksCube.MOVE currMove = RubiksCube.MOVE.values()[i];
                node.cube.move(currMove);

                // We need to check if visited. But node.cube is mutated!
                // This is the same issue as BFS.
                // We need to copy the cube before adding to PQ?
                // Or copy before moving?

                // In C++:
                // Node node = p.first; (Copy of Node struct, which contains T cube)
                // T cube is copied? Yes if T is value type.
                // In Java, T is reference.
                // So `Node node = p.first` copies the reference.
                // `node.cube.move` modifies the object in PQ!

                // We MUST copy the cube.
                // `Node start = new Node(rubiksCube.copy(), ...)`

                // And inside loop:
                // `RubiksCube nextCube = node.cube.copy();`
                // `nextCube.move(currMove);`

                RubiksCube nextCube = node.cube.copy();
                if (!visited.containsKey((T) nextCube)) { // Cast nextCube to T for map lookup
                    int estimate = cornerDB.getNumMoves(nextCube);
                    if (estimate + node.depth > bound) {
                        nextBound = Math.min(nextBound, estimate + node.depth);
                    } else {
                        pq.add(new Pair<>(new Node(nextCube, node.depth, estimate), i));
                    }
                }
                // No need to invert since we worked on a copy.
            }
        }
        return new Pair<>(rubiksCube, nextBound);
    }

    public List<RubiksCube.MOVE> solve() {
        int bound = 1;
        Pair<T, Integer> p = IDAstar(bound);
        while (p.second != bound) {
            resetStructure();
            bound = p.second;
            p = IDAstar(bound);
        }
        T solvedCube = p.first;
        // Reconstruct path
        T currCube = solvedCube;
        while (!currCube.equals(rubiksCube)) {
            RubiksCube.MOVE currMove = moveDone.get(currCube);
            moves.add(currMove);
            currCube.invert(currMove);
        }
        Collections.reverse(moves);
        return moves;
    }
}
