package rubikscube.main;

import rubikscube.model.RubiksCube;
import rubikscube.model.RubiksCubeBitboard;
import rubikscube.solver.IDAstarSolver;
import rubikscube.patterndatabase.CornerDBMaker;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // RubiksCube3dArray object3DArray = new RubiksCube3dArray();
        // RubiksCube1dArray object1dArray = new RubiksCube1dArray();
        // RubiksCubeBitboard objectBitboard = new RubiksCubeBitboard();

        // object3DArray.print();

        // if (object3DArray.isSolved()) System.out.println("SOLVED\n");
        // else System.out.println("NOT SOLVED\n");

        // if (object1dArray.isSolved()) System.out.println("SOLVED\n");
        // else System.out.println("NOT SOLVED\n");

        // if (objectBitboard.isSolved()) System.out.println("SOLVED\n");
        // else System.out.println("NOT SOLVED\n");

        // objectBitboard.u();
        // object3DArray.u();
        // object1dArray.u();
        // objectBitboard.print();
        // object3DArray.print();
        // object1dArray.print();

        // DFS Solver Testing
        // RubiksCube3dArray cube = new RubiksCube3dArray();
        // cube.print();
        // List<RubiksCube.MOVE> shuffle_moves = cube.randomShuffleCube(6);
        // for (RubiksCube.MOVE move : shuffle_moves)
        // System.out.print(RubiksCube.getMove(move) + " ");
        // System.out.println();
        // cube.print();

        // DFSSolver<RubiksCube3dArray> dfsSolver = new DFSSolver<>(cube, 8);
        // List<RubiksCube.MOVE> solve_moves = dfsSolver.solve();

        // for (RubiksCube.MOVE move : solve_moves)
        // System.out.print(RubiksCube.getMove(move) + " ");
        // System.out.println();
        // dfsSolver.rubiksCube.print(); // Note: rubiksCube field access might need
        // getter if private

        // BFS Solver Testing
        // RubiksCubeBitboard cube = new RubiksCubeBitboard();
        // cube.print();
        // List<RubiksCube.MOVE> shuffle_moves = cube.randomShuffleCube(6);
        // for (RubiksCube.MOVE move : shuffle_moves)
        // System.out.print(RubiksCube.getMove(move) + " ");
        // System.out.println();
        // cube.print();

        // BFSSolver<RubiksCubeBitboard> bfsSolver = new BFSSolver<>(cube);
        // List<RubiksCube.MOVE> solve_moves = bfsSolver.solve();

        // for (RubiksCube.MOVE move : solve_moves)
        // System.out.print(RubiksCube.getMove(move) + " ");
        // System.out.println();

        // IDDFS Solver Testing
        // RubiksCubeBitboard cube = new RubiksCubeBitboard();
        // cube.print();
        // List<RubiksCube.MOVE> shuffle_moves = cube.randomShuffleCube(7);
        // for (RubiksCube.MOVE move : shuffle_moves)
        // System.out.print(RubiksCube.getMove(move) + " ");
        // System.out.println();
        // cube.print();

        // IDDFSSolver<RubiksCubeBitboard> iddfsSolver = new IDDFSSolver<>(cube, 7);
        // List<RubiksCube.MOVE> solve_moves = iddfsSolver.solve();

        // for (RubiksCube.MOVE move : solve_moves)
        // System.out.print(RubiksCube.getMove(move) + " ");
        // System.out.println();

        // CornerDBMaker Testing
        String fileName = "cornerDepth5V1.txt";
        CornerDBMaker dbMaker = new CornerDBMaker(fileName, 0x99); // 0x99 is 153
        dbMaker.bfsAndStore();

        RubiksCubeBitboard cube = new RubiksCubeBitboard();
        List<RubiksCube.MOVE> shuffleMoves = cube.randomShuffleCube(13);
        cube.print();
        for (RubiksCube.MOVE move : shuffleMoves)
            System.out.print(RubiksCube.getMove(move) + " ");
        System.out.println();

        IDAstarSolver<RubiksCubeBitboard> idaStarSolver = new IDAstarSolver<>(cube, fileName);
        List<RubiksCube.MOVE> moves = idaStarSolver.solve();

        // idaStarSolver.rubiksCube.print(); // Need getter
        for (RubiksCube.MOVE move : moves)
            System.out.print(RubiksCube.getMove(move) + " ");
        System.out.println();
    }
}
