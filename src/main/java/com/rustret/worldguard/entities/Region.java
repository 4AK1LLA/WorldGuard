package com.rustret.worldguard.entities;

import java.util.UUID;

public class Region {
    public String regionName, ownerName;

    public UUID ownerId;

    public Coord pos1, pos2;

    public boolean pvp;

    public Region(String regionName, String ownerName, UUID ownerId, Coord pos1, Coord pos2) {
        this.regionName = regionName;
        this.ownerName = ownerName;
        this.ownerId = ownerId;
        this.pos1 = pos1;
        this.pos2 = pos2;
        pvp = true;
    }

    public void setFlag(String flag, boolean value) {
        if (flag.equals("pvp")) {
            pvp = value;
        }
    }
}
