package rubikscube.model;

public class RubiksCube3dArray extends RubiksCube {

    private char[][][] cube = new char[6][3][3];

    public RubiksCube3dArray() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    cube[i][j][k] = getColorLetter(COLOR.values()[i]);
                }
            }
        }
    }

    private void rotateFace(int ind) {
        char[][] temp_arr = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                temp_arr[i][j] = cube[ind][i][j];
            }
        }
        for (int i = 0; i < 3; i++)
            cube[ind][0][i] = temp_arr[2 - i][0];
        for (int i = 0; i < 3; i++)
            cube[ind][i][2] = temp_arr[0][i];
        for (int i = 0; i < 3; i++)
            cube[ind][2][2 - i] = temp_arr[i][2];
        for (int i = 0; i < 3; i++)
            cube[ind][2 - i][0] = temp_arr[2][2 - i];
    }

    @Override
    public COLOR getColor(FACE face, int row, int col) {
        char color = cube[face.ordinal()][row][col];
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
                    if (this.cube[i][j][k] == getColorLetter(COLOR.values()[i]))
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
            temp_arr[i] = cube[4][0][2 - i];
        for (int i = 0; i < 3; i++)
            cube[4][0][2 - i] = cube[1][0][2 - i];
        for (int i = 0; i < 3; i++)
            cube[1][0][2 - i] = cube[2][0][2 - i];
        for (int i = 0; i < 3; i++)
            cube[2][0][2 - i] = cube[3][0][2 - i];
        for (int i = 0; i < 3; i++)
            cube[3][0][2 - i] = temp_arr[i];
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
            temp_arr[i] = cube[0][i][0];
        for (int i = 0; i < 3; i++)
            cube[0][i][0] = cube[4][2 - i][2];
        for (int i = 0; i < 3; i++)
            cube[4][2 - i][2] = cube[5][i][0];
        for (int i = 0; i < 3; i++)
            cube[5][i][0] = cube[2][i][0];
        for (int i = 0; i < 3; i++)
            cube[2][i][0] = temp_arr[i];
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
            temp_arr[i] = cube[0][2][i];
        for (int i = 0; i < 3; i++)
            cube[0][2][i] = cube[1][2 - i][2];
        for (int i = 0; i < 3; i++)
            cube[1][2 - i][2] = cube[5][0][2 - i];
        for (int i = 0; i < 3; i++)
            cube[5][0][2 - i] = cube[3][i][0];
        for (int i = 0; i < 3; i++)
            cube[3][i][0] = temp_arr[i];
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
            temp_arr[i] = cube[0][2 - i][2];
        for (int i = 0; i < 3; i++)
            cube[0][2 - i][2] = cube[2][2 - i][2];
        for (int i = 0; i < 3; i++)
            cube[2][2 - i][2] = cube[5][2 - i][2];
        for (int i = 0; i < 3; i++)
            cube[5][2 - i][2] = cube[4][i][0];
        for (int i = 0; i < 3; i++)
            cube[4][i][0] = temp_arr[i];
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
            temp_arr[i] = cube[0][0][2 - i];
        for (int i = 0; i < 3; i++)
            cube[0][0][2 - i] = cube[3][2 - i][2];
        for (int i = 0; i < 3; i++)
            cube[3][2 - i][2] = cube[5][2][i];
        for (int i = 0; i < 3; i++)
            cube[5][2][i] = cube[1][i][0];
        for (int i = 0; i < 3; i++)
            cube[1][i][0] = temp_arr[i];
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
            temp_arr[i] = cube[2][2][i];
        for (int i = 0; i < 3; i++)
            cube[2][2][i] = cube[1][2][i];
        for (int i = 0; i < 3; i++)
            cube[1][2][i] = cube[4][2][i];
        for (int i = 0; i < 3; i++)
            cube[4][2][i] = cube[3][2][i];
        for (int i = 0; i < 3; i++)
            cube[3][2][i] = temp_arr[i];
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
        RubiksCube3dArray other = (RubiksCube3dArray) obj;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    if (cube[i][j][k] != other.cube[i][j][k])
                        return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    str.append(cube[i][j][k]);
                }
            }
        }
        return str.toString().hashCode();
    }

    @Override
    public RubiksCube copy() {
        RubiksCube3dArray newCube = new RubiksCube3dArray();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    newCube.cube[i][j][k] = this.cube[i][j][k];
                }
            }
        }
        return newCube;
    }
}
