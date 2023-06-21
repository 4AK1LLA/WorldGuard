package com.rustret.worldguard.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Region {
    public String ownerName;

    public UUID ownerId;

    public Coord pos1, pos2;

    public List<Flag> flags;

    public Region(String ownerName, UUID ownerId, Coord pos1, Coord pos2) {
        this.ownerName = ownerName;
        this.ownerId = ownerId;
        this.pos1 = pos1;
        this.pos2 = pos2;

        this.flags = new ArrayList<>(); ///
    }
}
