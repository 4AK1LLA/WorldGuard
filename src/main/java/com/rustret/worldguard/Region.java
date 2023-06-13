package com.rustret.worldguard;

import com.rustret.worldguard.coordinates.Coord;

public class Region {
    public String ownerName;

    public String ownerId;

    public Coord pos1;

    public Coord pos2;

    public Region(String ownerName, String ownerId, Coord pos1, Coord pos2) {
        this.ownerName = ownerName;
        this.ownerId = ownerId;
        this.pos1 = pos1;
        this.pos2 = pos2;
    }
}
