package rubikscube.model;

public class RubiksCubeBitboard extends RubiksCube {

    public long[] bitboard = new long[6];
    private long[] solved_side_config = new long[6];

    private static final int[][] arr = {
            { 0, 1, 2 },
            { 7, 8, 3 },
            { 6, 5, 4 }
    };

    private static final long one_8 = (1L << 8) - 1;
    private static final long one_24 = (1L << 24) - 1;

    public RubiksCubeBitboard() {
        // System.out.println("one_8: " + Long.toHexString(one_8));
        // System.out.println("one_24: " + Long.toHexString(one_24));
        for (int side = 0; side < 6; side++) {
            long clr = 1L << side;
            bitboard[side] = 0;
            for (int faceIdx = 0; faceIdx < 8; faceIdx++) {
                bitboard[side] |= clr << (8 * faceIdx);
            }
            solved_side_config[side] = bitboard[side];
        }
    }

    public RubiksCubeBitboard(RubiksCubeBitboard other) {
        System.arraycopy(other.bitboard, 0, this.bitboard, 0, 6);
        System.arraycopy(other.solved_side_config, 0, this.solved_side_config, 0, 6);
    }

    private void rotateFace(int ind) {
        long side = bitboard[ind];
        side = side >>> (8 * 6);
        bitboard[ind] = (bitboard[ind] << 16) | (side);
    }

    private void rotateSide(int s1, int s1_1, int s1_2, int s1_3, int s2, int s2_1, int s2_2, int s2_3) {
        long clr1 = (bitboard[s2] & (one_8 << (8 * s2_1))) >>> (8 * s2_1);
        long clr2 = (bitboard[s2] & (one_8 << (8 * s2_2))) >>> (8 * s2_2);
        long clr3 = (bitboard[s2] & (one_8 << (8 * s2_3))) >>> (8 * s2_3);

        bitboard[s1] = (bitboard[s1] & ~(one_8 << (8 * s1_1))) | (clr1 << (8 * s1_1));
        bitboard[s1] = (bitboard[s1] & ~(one_8 << (8 * s1_2))) | (clr2 << (8 * s1_2));
        bitboard[s1] = (bitboard[s1] & ~(one_8 << (8 * s1_3))) | (clr3 << (8 * s1_3));
    }

    private int get5bitCorner(String corner) {
        int ret = 0;
        StringBuilder actual_str = new StringBuilder();
        for (char c : corner.toCharArray()) {
            if (c != 'W' && c != 'Y')
                continue;
            actual_str.append(c);
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

        if (corner.charAt(1) == actual_str.charAt(0)) {
            ret |= (1 << 3);
        } else if (corner.charAt(2) == actual_str.charAt(0)) {
            ret |= (1 << 4);
        }
        return ret;
    }

    public long getCorners() {
        long ret = 0;
        String top_front_right = "";
        top_front_right += getColorLetter(getColor(FACE.UP, 2, 2));
        top_front_right += getColorLetter(getColor(FACE.FRONT, 0, 2));
        top_front_right += getColorLetter(getColor(FACE.RIGHT, 0, 0));

        String top_front_left = "";
        top_front_left += getColorLetter(getColor(FACE.UP, 2, 0));
        top_front_left += getColorLetter(getColor(FACE.FRONT, 0, 0));
        top_front_left += getColorLetter(getColor(FACE.LEFT, 0, 2));

        String top_back_left = "";
        top_back_left += getColorLetter(getColor(FACE.UP, 0, 0));
        top_back_left += getColorLetter(getColor(FACE.BACK, 0, 2));
        top_back_left += getColorLetter(getColor(FACE.LEFT, 0, 0));

        String top_back_right = "";
        top_back_right += getColorLetter(getColor(FACE.UP, 0, 2));
        top_back_right += getColorLetter(getColor(FACE.BACK, 0, 0));
        top_back_right += getColorLetter(getColor(FACE.RIGHT, 0, 2));

        String bottom_front_right = "";
        bottom_front_right += getColorLetter(getColor(FACE.DOWN, 0, 2));
        bottom_front_right += getColorLetter(getColor(FACE.FRONT, 2, 2));
        bottom_front_right += getColorLetter(getColor(FACE.RIGHT, 2, 0));

        String bottom_front_left = "";
        bottom_front_left += getColorLetter(getColor(FACE.DOWN, 0, 0));
        bottom_front_left += getColorLetter(getColor(FACE.FRONT, 2, 0));
        bottom_front_left += getColorLetter(getColor(FACE.LEFT, 2, 2));

        String bottom_back_right = "";
        bottom_back_right += getColorLetter(getColor(FACE.DOWN, 2, 2));
        bottom_back_right += getColorLetter(getColor(FACE.BACK, 2, 0));
        bottom_back_right += getColorLetter(getColor(FACE.RIGHT, 2, 2));

        String bottom_back_left = "";
        bottom_back_left += getColorLetter(getColor(FACE.DOWN, 2, 0));
        bottom_back_left += getColorLetter(getColor(FACE.BACK, 2, 2));
        bottom_back_left += getColorLetter(getColor(FACE.LEFT, 2, 0));

        ret |= get5bitCorner(top_front_right);
        ret = ret << 5;

        ret |= get5bitCorner(top_front_left);
        ret = ret << 5;

        ret |= get5bitCorner(top_back_right);
        ret = ret << 5;

        ret |= get5bitCorner(top_back_left);
        ret = ret << 5;

        ret |= get5bitCorner(bottom_front_right);
        ret = ret << 5;

        ret |= get5bitCorner(bottom_front_left);
        ret = ret << 5;

        ret |= get5bitCorner(bottom_back_right);
        ret = ret << 5;

        ret |= get5bitCorner(bottom_back_left);
        ret = ret << 5;

        return ret;
    }

    @Override
    public COLOR getColor(FACE face, int row, int col) {
        int idx = arr[row][col];
        if (idx == 8)
            return COLOR.values()[face.ordinal()];

        long side = bitboard[face.ordinal()];
        long color = (side >>> (8 * idx)) & one_8;

        int bit_pos = 0;
        while (color != 0) {
            color = color >>> 1;
            bit_pos++;
        }
        return COLOR.values()[bit_pos - 1];
    }

    @Override
    public boolean isSolved() {
        for (int i = 0; i < 6; i++) {
            if (solved_side_config[i] != bitboard[i])
                return false;
        }
        return true;
    }

    @Override
    public RubiksCube u() {
        rotateFace(0);
        long temp = bitboard[2] & one_24;
        bitboard[2] = (bitboard[2] & ~one_24) | (bitboard[3] & one_24);
        bitboard[3] = (bitboard[3] & ~one_24) | (bitboard[4] & one_24);
        bitboard[4] = (bitboard[4] & ~one_24) | (bitboard[1] & one_24);
        bitboard[1] = (bitboard[1] & ~one_24) | temp;
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
        long clr1 = (bitboard[2] & (one_8 << (8 * 0))) >>> (8 * 0);
        long clr2 = (bitboard[2] & (one_8 << (8 * 6))) >>> (8 * 6);
        long clr3 = (bitboard[2] & (one_8 << (8 * 7))) >>> (8 * 7);

        rotateSide(2, 0, 7, 6, 0, 0, 7, 6);
        rotateSide(0, 0, 7, 6, 4, 4, 3, 2);
        rotateSide(4, 4, 3, 2, 5, 0, 7, 6);

        bitboard[5] = (bitboard[5] & ~(one_8 << (8 * 0))) | (clr1 << (8 * 0));
        bitboard[5] = (bitboard[5] & ~(one_8 << (8 * 6))) | (clr2 << (8 * 6));
        bitboard[5] = (bitboard[5] & ~(one_8 << (8 * 7))) | (clr3 << (8 * 7));
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
        long clr1 = (bitboard[0] & (one_8 << (8 * 4))) >>> (8 * 4);
        long clr2 = (bitboard[0] & (one_8 << (8 * 5))) >>> (8 * 5);
        long clr3 = (bitboard[0] & (one_8 << (8 * 6))) >>> (8 * 6);

        rotateSide(0, 4, 5, 6, 1, 2, 3, 4);
        rotateSide(1, 2, 3, 4, 5, 0, 1, 2);
        rotateSide(5, 0, 1, 2, 3, 6, 7, 0);

        bitboard[3] = (bitboard[3] & ~(one_8 << (8 * 6))) | (clr1 << (8 * 6));
        bitboard[3] = (bitboard[3] & ~(one_8 << (8 * 7))) | (clr2 << (8 * 7));
        bitboard[3] = (bitboard[3] & ~(one_8 << (8 * 0))) | (clr3 << (8 * 0));
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
        long clr1 = (bitboard[0] & (one_8 << (8 * 2))) >>> (8 * 2);
        long clr2 = (bitboard[0] & (one_8 << (8 * 3))) >>> (8 * 3);
        long clr3 = (bitboard[0] & (one_8 << (8 * 4))) >>> (8 * 4);

        rotateSide(0, 2, 3, 4, 2, 2, 3, 4);
        rotateSide(2, 2, 3, 4, 5, 2, 3, 4);
        rotateSide(5, 2, 3, 4, 4, 6, 7, 0);

        bitboard[4] = (bitboard[4] & ~(one_8 << (8 * 6))) | (clr1 << (8 * 6));
        bitboard[4] = (bitboard[4] & ~(one_8 << (8 * 7))) | (clr2 << (8 * 7));
        bitboard[4] = (bitboard[4] & ~(one_8 << (8 * 0))) | (clr3 << (8 * 0));
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
        long clr1 = (bitboard[0] & (one_8 << (8 * 0))) >>> (8 * 0);
        long clr2 = (bitboard[0] & (one_8 << (8 * 1))) >>> (8 * 1);
        long clr3 = (bitboard[0] & (one_8 << (8 * 2))) >>> (8 * 2);

        rotateSide(0, 0, 1, 2, 3, 2, 3, 4);
        rotateSide(3, 2, 3, 4, 5, 4, 5, 6);
        rotateSide(5, 4, 5, 6, 1, 6, 7, 0);

        bitboard[1] = (bitboard[1] & ~(one_8 << (8 * 6))) | (clr1 << (8 * 6));
        bitboard[1] = (bitboard[1] & ~(one_8 << (8 * 7))) | (clr2 << (8 * 7));
        bitboard[1] = (bitboard[1] & ~(one_8 << (8 * 0))) | (clr3 << (8 * 0));
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
        long clr1 = (bitboard[2] & (one_8 << (8 * 4))) >>> (8 * 4);
        long clr2 = (bitboard[2] & (one_8 << (8 * 5))) >>> (8 * 5);
        long clr3 = (bitboard[2] & (one_8 << (8 * 6))) >>> (8 * 6);

        rotateSide(2, 4, 5, 6, 1, 4, 5, 6);
        rotateSide(1, 4, 5, 6, 4, 4, 5, 6);
        rotateSide(4, 4, 5, 6, 3, 4, 5, 6);

        bitboard[3] = (bitboard[3] & ~(one_8 << (8 * 4))) | (clr1 << (8 * 4));
        bitboard[3] = (bitboard[3] & ~(one_8 << (8 * 5))) | (clr2 << (8 * 5));
        bitboard[3] = (bitboard[3] & ~(one_8 << (8 * 6))) | (clr3 << (8 * 6));
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
        RubiksCubeBitboard other = (RubiksCubeBitboard) obj;
        for (int i = 0; i < 6; i++) {
            if (bitboard[i] != other.bitboard[i])
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (long l : bitboard) {
            hash ^= Long.hashCode(l);
        }
        return hash;
    }

    @Override
    public RubiksCube copy() {
        return new RubiksCubeBitboard(this);
    }

    public void printBitboard() {
        for (int i = 0; i < 6; i++) {
            System.out.println("Face " + i + ": " + Long.toHexString(bitboard[i]));
        }
    }
}
