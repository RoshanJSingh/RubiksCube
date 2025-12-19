package rubikscube.patterndatabase;

import rubikscube.model.RubiksCube;

public class CornerPatternDatabase extends PatternDatabase {

    private final PermutationIndexer permIndexer;

    public CornerPatternDatabase() {
        super(100179840);
        this.permIndexer = new PermutationIndexer(8, 8);
    }

    public CornerPatternDatabase(int init_val) {
        super(100179840, init_val);
        this.permIndexer = new PermutationIndexer(8, 8);
    }

    @Override
    public int getDatabaseIndex(RubiksCube cube) {
        int[] cornerPerm = {
                cube.getCornerIndex(0),
                cube.getCornerIndex(1),
                cube.getCornerIndex(2),
                cube.getCornerIndex(3),
                cube.getCornerIndex(4),
                cube.getCornerIndex(5),
                cube.getCornerIndex(6),
                cube.getCornerIndex(7)
        };

        int rank = this.permIndexer.rank(cornerPerm);

        int[] cornerOrientations = {
                cube.getCornerOrientation(0),
                cube.getCornerOrientation(1),
                cube.getCornerOrientation(2),
                cube.getCornerOrientation(3),
                cube.getCornerOrientation(4),
                cube.getCornerOrientation(5),
                cube.getCornerOrientation(6)
        };

        int orientationNum = cornerOrientations[0] * 729 +
                cornerOrientations[1] * 243 +
                cornerOrientations[2] * 81 +
                cornerOrientations[3] * 27 +
                cornerOrientations[4] * 9 +
                cornerOrientations[5] * 3 +
                cornerOrientations[6];

        return (rank * 2187) + orientationNum;
    }
}
