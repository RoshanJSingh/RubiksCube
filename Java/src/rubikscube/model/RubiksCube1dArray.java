package rubikscube.model;

import java.util.Arrays;

public class RubiksCube1dArray extends RubiksCube {

    private char[] cube = new char[54];

    public RubiksCube1dArray() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    cube[i * 9 + j * 3 + k] = getColorLetter(COLOR.values()[i]);
                }
            }
        }
    }

    private static int getIndex(int ind, int row, int col) {
        return (ind * 9) + (row * 3) + col;
    }

    private void rotateFace(int ind) {
        char[] temp_arr = new char[9];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                temp_arr[i * 3 + j] = cube[getIndex(ind, i, j)];
            }
        }
        for (int i = 0; i < 3; i++)
            cube[getIndex(ind, 0, i)] = temp_arr[getIndex(0, 2 - i, 0)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(ind, i, 2)] = temp_arr[getIndex(0, 0, i)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(ind, 2, 2 - i)] = temp_arr[getIndex(0, i, 2)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(ind, 2 - i, 0)] = temp_arr[getIndex(0, 2, 2 - i)];
    }

    @Override
    public COLOR getColor(FACE face, int row, int col) {
        char color = cube[getIndex(face.ordinal(), row, col)];
        switch (color) {
            case 'B':
                return COLOR.BLUE;
            case 'R':
                return COLOR.RED;
            case 'G':
                return COLOR.GREEN;
            case 'O':
                return COLOR.ORANGE;
            case 'Y':
                return COLOR.YELLOW;
            default:
                return COLOR.WHITE;
        }
    }

    @Override
    public boolean isSolved() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    if (this.cube[getIndex(i, j, k)] == getColorLetter(COLOR.values()[i]))
                        continue;
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public RubiksCube u() {
        rotateFace(0);
        char[] temp_arr = new char[3];
        for (int i = 0; i < 3; i++)
            temp_arr[i] = cube[getIndex(4, 0, 2 - i)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(4, 0, 2 - i)] = cube[getIndex(1, 0, 2 - i)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(1, 0, 2 - i)] = cube[getIndex(2, 0, 2 - i)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(2, 0, 2 - i)] = cube[getIndex(3, 0, 2 - i)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(3, 0, 2 - i)] = temp_arr[i];
        return this;
    }

    @Override
    public RubiksCube uPrime() {
        u();
        u();
        u();
        return this;
    }

    @Override
    public RubiksCube u2() {
        u();
        u();
        return this;
    }

    @Override
    public RubiksCube l() {
        rotateFace(1);
        char[] temp_arr = new char[3];
        for (int i = 0; i < 3; i++)
            temp_arr[i] = cube[getIndex(0, i, 0)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(0, i, 0)] = cube[getIndex(4, 2 - i, 2)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(4, 2 - i, 2)] = cube[getIndex(5, i, 0)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(5, i, 0)] = cube[getIndex(2, i, 0)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(2, i, 0)] = temp_arr[i];
        return this;
    }

    @Override
    public RubiksCube lPrime() {
        l();
        l();
        l();
        return this;
    }

    @Override
    public RubiksCube l2() {
        l();
        l();
        return this;
    }

    @Override
    public RubiksCube f() {
        rotateFace(2);
        char[] temp_arr = new char[3];
        for (int i = 0; i < 3; i++)
            temp_arr[i] = cube[getIndex(0, 2, i)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(0, 2, i)] = cube[getIndex(1, 2 - i, 2)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(1, 2 - i, 2)] = cube[getIndex(5, 0, 2 - i)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(5, 0, 2 - i)] = cube[getIndex(3, i, 0)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(3, i, 0)] = temp_arr[i];
        return this;
    }

    @Override
    public RubiksCube fPrime() {
        f();
        f();
        f();
        return this;
    }

    @Override
    public RubiksCube f2() {
        f();
        f();
        return this;
    }

    @Override
    public RubiksCube r() {
        rotateFace(3);
        char[] temp_arr = new char[3];
        for (int i = 0; i < 3; i++)
            temp_arr[i] = cube[getIndex(0, 2 - i, 2)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(0, 2 - i, 2)] = cube[getIndex(2, 2 - i, 2)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(2, 2 - i, 2)] = cube[getIndex(5, 2 - i, 2)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(5, 2 - i, 2)] = cube[getIndex(4, i, 0)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(4, i, 0)] = temp_arr[i];
        return this;
    }

    @Override
    public RubiksCube rPrime() {
        r();
        r();
        r();
        return this;
    }

    @Override
    public RubiksCube r2() {
        r();
        r();
        return this;
    }

    @Override
    public RubiksCube b() {
        rotateFace(4);
        char[] temp_arr = new char[3];
        for (int i = 0; i < 3; i++)
            temp_arr[i] = cube[getIndex(0, 0, 2 - i)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(0, 0, 2 - i)] = cube[getIndex(3, 2 - i, 2)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(3, 2 - i, 2)] = cube[getIndex(5, 2, i)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(5, 2, i)] = cube[getIndex(1, i, 0)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(1, i, 0)] = temp_arr[i];
        return this;
    }

    @Override
    public RubiksCube bPrime() {
        b();
        b();
        b();
        return this;
    }

    @Override
    public RubiksCube b2() {
        b();
        b();
        return this;
    }

    @Override
    public RubiksCube d() {
        rotateFace(5);
        char[] temp_arr = new char[3];
        for (int i = 0; i < 3; i++)
            temp_arr[i] = cube[getIndex(2, 2, i)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(2, 2, i)] = cube[getIndex(1, 2, i)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(1, 2, i)] = cube[getIndex(4, 2, i)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(4, 2, i)] = cube[getIndex(3, 2, i)];
        for (int i = 0; i < 3; i++)
            cube[getIndex(3, 2, i)] = temp_arr[i];
        return this;
    }

    @Override
    public RubiksCube dPrime() {
        d();
        d();
        d();
        return this;
    }

    @Override
    public RubiksCube d2() {
        d();
        d();
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        RubiksCube1dArray other = (RubiksCube1dArray) obj;
        return Arrays.equals(cube, other.cube);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(cube);
    }

    @Override
    public RubiksCube copy() {
        RubiksCube1dArray newCube = new RubiksCube1dArray();
        System.arraycopy(this.cube, 0, newCube.cube, 0, 54);
        return newCube;
    }
}
