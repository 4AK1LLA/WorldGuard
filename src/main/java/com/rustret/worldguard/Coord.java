package com.rustret.worldguard;

public class Coord {
    public int x, y, z;

    public Coord(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return x + " " + y + " " + z;
    }
}
