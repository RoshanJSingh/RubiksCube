package rubikscube.patterndatabase;

import rubikscube.model.RubiksCube;
import java.io.*;

public abstract class PatternDatabase {

    protected NibbleArray database;
    protected int size;
    protected int numItems;

    public PatternDatabase(int size) {
        this(size, 0xFF);
    }

    public PatternDatabase(int size, int init_val) {
        this.database = new NibbleArray(size, init_val);
        this.size = size;
        this.numItems = 0;
    }

    public abstract int getDatabaseIndex(RubiksCube cube);

    public boolean setNumMoves(RubiksCube cube, int numMoves) {
        return this.setNumMoves(this.getDatabaseIndex(cube), numMoves);
    }

    public boolean setNumMoves(int ind, int numMoves) {
        int oldMoves = this.getNumMoves(ind);

        if (oldMoves == 0xF) {
            this.numItems++;
        }

        if (oldMoves > numMoves) {
            this.database.set(ind, numMoves);
            return true;
        }
        return false;
    }

    public int getNumMoves(RubiksCube cube) {
        return this.getNumMoves(this.getDatabaseIndex(cube));
    }

    public int getNumMoves(int ind) {
        return this.database.get(ind);
    }

    public int getSize() {
        return this.size;
    }

    public int getNumItems() {
        return this.numItems;
    }

    public boolean isFull() {
        return this.numItems == this.size;
    }

    public void toFile(String filePath) throws IOException {
        try (FileOutputStream writer = new FileOutputStream(filePath)) {
            writer.write(this.database.data());
        }
    }

    public boolean fromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists())
            return false;

        try (FileInputStream reader = new FileInputStream(file)) {
            if (file.length() != this.database.storageSize()) {
                return false;
            }
            reader.read(this.database.data());
            this.numItems = this.size;
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public byte[] inflate() {
        byte[] inflated = new byte[this.size];
        this.database.inflate(inflated);
        return inflated;
    }

    public void reset() {
        if (this.numItems != 0) {
            this.database.reset(0xFF);
            this.numItems = 0;
        }
    }
}
