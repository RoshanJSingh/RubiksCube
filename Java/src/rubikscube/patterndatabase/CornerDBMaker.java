package rubikscube.patterndatabase;

import rubikscube.model.RubiksCube;
import rubikscube.model.RubiksCubeBitboard;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

public class CornerDBMaker {
    private final String fileName;
    private final CornerPatternDatabase cornerDB;

    public CornerDBMaker(String fileName) {
        this.fileName = fileName;
        this.cornerDB = new CornerPatternDatabase();
    }

    public CornerDBMaker(String fileName, int init_val) {
        this.fileName = fileName;
        this.cornerDB = new CornerPatternDatabase(init_val);
    }

    public boolean bfsAndStore() {
        RubiksCubeBitboard cube = new RubiksCubeBitboard();
        Queue<RubiksCubeBitboard> q = new ArrayDeque<>();
        q.add(cube);
        cornerDB.setNumMoves(cube, 0);
        int curr_depth = 0;

        while (!q.isEmpty()) {
            int n = q.size();
            // System.out.println("Depth: " + curr_depth + ", Queue size: " + n);

            for (int i = 0; i < n; ++i) {
                RubiksCubeBitboard node = q.poll();

                for (RubiksCube.MOVE move : RubiksCube.MOVE.values()) {
                    node.move(move);

                    if (cornerDB.getNumMoves(node) > curr_depth + 1) {
                        cornerDB.setNumMoves(node, curr_depth + 1);
                        q.add(new RubiksCubeBitboard(node));
                    }
                    node.invert(move);
                }
            }
            curr_depth++;
            if (curr_depth == 9)
                break;
        }

        try {
            cornerDB.toFile(fileName);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
