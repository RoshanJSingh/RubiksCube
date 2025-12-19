package rubikscube.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A base class for all Rubik's Cube Model. There are various representation for
 * Rubik's Cube.
 * Each one has it's own special ways of definitions. This class provides a
 * shared functionality
 * between all models.
 */
public abstract class RubiksCube {

    public enum FACE {
        UP,
        LEFT,
        FRONT,
        RIGHT,
        BACK,
        DOWN
    }

    public enum COLOR {
        WHITE,
        GREEN,
        RED,
        BLUE,
        ORANGE,
        YELLOW
    }

    public enum MOVE {
        L, LPRIME, L2,
        R, RPRIME, R2,
        U, UPRIME, U2,
        D, DPRIME, D2,
        F, FPRIME, F2,
        B, BPRIME, B2
    }

    /*
     * Returns the color of the cell at (row, col) in face.
     * If Rubik's Cube face is pointing at you, then the row numbering starts from
     * the
     * top to bottom, and column numbering starts from the left to right.
     * The rows and columns are 0-indexed.
     */
    public abstract COLOR getColor(FACE face, int row, int col);

    /*
     * Returns the first letter of the given COLOR
     * Eg: For COLOR.GREEN, it returns 'G'
     */
    public static char getColorLetter(COLOR color) {
        switch (color) {
            case BLUE:
                return 'B';
            case GREEN:
                return 'G';
            case RED:
                return 'R';
            case YELLOW:
                return 'Y';
            case WHITE:
                return 'W';
            case ORANGE:
                return 'O';
            default:
                return ' ';
        }
    }

    /*
     * Returns true if the Rubik Cube is solved, otherwise returns false.
     */
    public abstract boolean isSolved();

    /*
     * Returns the move in the string format.
     */
    public static String getMove(MOVE ind) {
        switch (ind) {
            case L:
                return "L";
            case LPRIME:
                return "L'";
            case L2:
                return "L2";
            case R:
                return "R";
            case RPRIME:
                return "R'";
            case R2:
                return "R2";
            case U:
                return "U";
            case UPRIME:
                return "U'";
            case U2:
                return "U2";
            case D:
                return "D";
            case DPRIME:
                return "D'";
            case D2:
                return "D2";
            case F:
                return "F";
            case FPRIME:
                return "F'";
            case F2:
                return "F2";
            case B:
                return "B";
            case BPRIME:
                return "B'";
            case B2:
                return "B2";
            default:
                return "";
        }
    }

    /*
     * Print the Rubik Cube in Planar format.
     */
    public void print() {
        System.out.println("Rubik's Cube:");
        for (int row = 0; row <= 2; row++) {
            for (int i = 0; i < 7; i++)
                System.out.print(" ");
            for (int col = 0; col <= 2; col++) {
                System.out.print(getColorLetter(getColor(FACE.UP, row, col)) + " ");
            }
            System.out.println();
        }

        System.out.println();

        for (int row = 0; row <= 2; row++) {
            for (int col = 0; col <= 2; col++)
                System.out.print(getColorLetter(getColor(FACE.LEFT, row, col)) + " ");
            System.out.print(" ");
            for (int col = 0; col <= 2; col++)
                System.out.print(getColorLetter(getColor(FACE.FRONT, row, col)) + " ");
            System.out.print(" ");
            for (int col = 0; col <= 2; col++)
                System.out.print(getColorLetter(getColor(FACE.RIGHT, row, col)) + " ");
            System.out.print(" ");
            for (int col = 0; col <= 2; col++)
                System.out.print(getColorLetter(getColor(FACE.BACK, row, col)) + " ");
            System.out.println();
        }

        System.out.println();

        for (int row = 0; row <= 2; row++) {
            for (int i = 0; i < 7; i++)
                System.out.print(" ");
            for (int col = 0; col <= 2; col++) {
                System.out.print(getColorLetter(getColor(FACE.DOWN, row, col)) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /*
     * Randomly shuffle the cube with 'times' moves and returns the moves performed.
     */
    public List<MOVE> randomShuffleCube(int times) {
        List<MOVE> moves = new ArrayList<>();
        Random rand = new Random();
        MOVE[] allMoves = MOVE.values();
        for (int i = 0; i < times; i++) {
            MOVE move = allMoves[rand.nextInt(allMoves.length)];
            moves.add(move);
            this.move(move);
        }
        return moves;
    }

    /*
     * Perform moves on the Rubik Cube
     */
    public RubiksCube move(MOVE ind) {
        switch (ind) {
            case L:
                return l();
            case LPRIME:
                return lPrime();
            case L2:
                return l2();
            case R:
                return r();
            case RPRIME:
                return rPrime();
            case R2:
                return r2();
            case U:
                return u();
            case UPRIME:
                return uPrime();
            case U2:
                return u2();
            case D:
                return d();
            case DPRIME:
                return dPrime();
            case D2:
                return d2();
            case F:
                return f();
            case FPRIME:
                return fPrime();
            case F2:
                return f2();
            case B:
                return b();
            case BPRIME:
                return bPrime();
            case B2:
                return b2();
            default:
                return this;
        }
    }

    /*
     * Invert a move
     */
    public RubiksCube invert(MOVE ind) {
        switch (ind) {
            case L:
                return lPrime();
            case LPRIME:
                return l();
            case L2:
                return l2();
            case R:
                return rPrime();
            case RPRIME:
                return r();
            case R2:
                return r2();
            case U:
                return uPrime();
            case UPRIME:
                return u();
            case U2:
                return u2();
            case D:
                return dPrime();
            case DPRIME:
                return d();
            case D2:
                return d2();
            case F:
                return fPrime();
            case FPRIME:
                return f();
            case F2:
                return f2();
            case B:
                return bPrime();
            case BPRIME:
                return b();
            case B2:
                return b2();
            default:
                return this;
        }
    }

    public abstract RubiksCube f();

    public abstract RubiksCube fPrime();

    public abstract RubiksCube f2();

    public abstract RubiksCube u();

    public abstract RubiksCube uPrime();

    public abstract RubiksCube u2();

    public abstract RubiksCube l();

    public abstract RubiksCube lPrime();

    public abstract RubiksCube l2();

    public abstract RubiksCube r();

    public abstract RubiksCube d();

    public abstract RubiksCube dPrime();

    public abstract RubiksCube d2();

    public abstract RubiksCube rPrime();

    public abstract RubiksCube r2();

    public abstract RubiksCube b();

    public abstract RubiksCube bPrime();

    public abstract RubiksCube b2();

    public String getCornerColorString(int ind) {
        StringBuilder str = new StringBuilder();

        switch (ind) {
            // UFR
            case 0:
                str.append(getColorLetter(getColor(FACE.UP, 2, 2)));
                str.append(getColorLetter(getColor(FACE.FRONT, 0, 2)));
                str.append(getColorLetter(getColor(FACE.RIGHT, 0, 0)));
                break;

            // UFL
            case 1:
                str.append(getColorLetter(getColor(FACE.UP, 2, 0)));
                str.append(getColorLetter(getColor(FACE.FRONT, 0, 0)));
                str.append(getColorLetter(getColor(FACE.LEFT, 0, 2)));
                break;

            // UBL
            case 2:
                str.append(getColorLetter(getColor(FACE.UP, 0, 0)));
                str.append(getColorLetter(getColor(FACE.BACK, 0, 2)));
                str.append(getColorLetter(getColor(FACE.LEFT, 0, 0)));
                break;

            // UBR
            case 3:
                str.append(getColorLetter(getColor(FACE.UP, 0, 2)));
                str.append(getColorLetter(getColor(FACE.BACK, 0, 0)));
                str.append(getColorLetter(getColor(FACE.RIGHT, 0, 2)));
                break;

            // DFR
            case 4:
                str.append(getColorLetter(getColor(FACE.DOWN, 0, 2)));
                str.append(getColorLetter(getColor(FACE.FRONT, 2, 2)));
                str.append(getColorLetter(getColor(FACE.RIGHT, 2, 0)));
                break;

            // DFL
            case 5:
                str.append(getColorLetter(getColor(FACE.DOWN, 0, 0)));
                str.append(getColorLetter(getColor(FACE.FRONT, 2, 0)));
                str.append(getColorLetter(getColor(FACE.LEFT, 2, 2)));
                break;

            // DBR
            case 6:
                str.append(getColorLetter(getColor(FACE.DOWN, 2, 2)));
                str.append(getColorLetter(getColor(FACE.BACK, 2, 0)));
                str.append(getColorLetter(getColor(FACE.RIGHT, 2, 2)));
                break;

            // DBL
            case 7:
                str.append(getColorLetter(getColor(FACE.DOWN, 2, 0)));
                str.append(getColorLetter(getColor(FACE.BACK, 2, 2)));
                str.append(getColorLetter(getColor(FACE.LEFT, 2, 0)));
                break;
        }
        return str.toString();
    }

    public int getCornerIndex(int ind) {
        String corner = getCornerColorString(ind);
        int ret = 0;

        for (char c : corner.toCharArray()) {
            if (c != 'W' && c != 'Y')
                continue;
            if (c == 'Y') {
                ret |= (1 << 2);
            }
        }

        for (char c : corner.toCharArray()) {
            if (c != 'R' && c != 'O')
                continue;
            if (c == 'O') {
                ret |= (1 << 1);
            }
        }

        for (char c : corner.toCharArray()) {
            if (c != 'B' && c != 'G')
                continue;
            if (c == 'G') {
                ret |= (1 << 0);
            }
        }
        return ret;
    }

    public int getCornerOrientation(int ind) {
        String corner = getCornerColorString(ind);
        String actual_str = "";

        for (char c : corner.toCharArray()) {
            if (c != 'W' && c != 'Y')
                continue;
            actual_str += c;
        }

        if (actual_str.length() == 0) {
            System.out.println("Error in getCornerOrientation: " + ind);
            System.out.println("Corner string: " + corner);
            this.print();
            throw new RuntimeException("Invalid corner string");
        }

        if (corner.charAt(1) == actual_str.charAt(0)) {
            return 1;
        } else if (corner.charAt(2) == actual_str.charAt(0)) {
            return 2;
        } else {
            return 0;
        }
    }

    public abstract RubiksCube copy();
}
