package rubikscube.patterndatabase;

import java.util.Arrays;

public class NibbleArray {
    private final int size;
    private final byte[] arr;

    public NibbleArray(int size, int val) {
        this.size = size;
        this.arr = new byte[size / 2 + 1];
        Arrays.fill(this.arr, (byte) val);
    }

    public int get(int pos) {
        int i = pos / 2;
        int val = arr[i] & 0xFF; // Convert to unsigned int

        // Odd pos: last 4 bits
        if (pos % 2 != 0) {
            return val & 0x0F;
        }
        // Even pos: first 4 bits from the left
        else {
            return (val >> 4) & 0x0F;
        }
    }

    public void set(int pos, int val) {
        int i = pos / 2;
        int currVal = arr[i] & 0xFF;

        if (pos % 2 != 0) {
            arr[i] = (byte) ((currVal & 0xF0) | (val & 0x0F));
        } else {
            arr[i] = (byte) ((currVal & 0x0F) | (val << 4));
        }
    }

    public byte[] data() {
        return this.arr;
    }

    public int storageSize() {
        return this.arr.length;
    }

    public void inflate(byte[] dest) {
        for (int i = 0; i < this.size; ++i)
            dest[i] = (byte) this.get(i);
    }

    public void reset(int val) {
        Arrays.fill(this.arr, (byte) val);
    }
}
