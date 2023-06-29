package com.rustret.worldguard.entities;

import cn.nukkit.math.Vector3;

import java.util.List;
import java.util.UUID;

public class Region {
    public String regionName, ownerName;
    public UUID ownerId;
    public List<UUID> memberIds;
    public Vector3[] coordinates;
    public boolean pvp;

    public Region(String regionName, String ownerName, UUID ownerId, Vector3[] coordinates, List<UUID> memberIds) {
        this.regionName = regionName;
        this.ownerName = ownerName;
        this.ownerId = ownerId;
        this.coordinates = coordinates;
        this.memberIds = memberIds;
        pvp = true;
    }

    public void setFlag(String flag, boolean value) {
        if (flag.equals("pvp")) {
            pvp = value;
        }
    }

    public boolean getFlag(String flag) {
        if (flag.equals("pvp")) {
            return pvp;
        }
        throw new IllegalArgumentException("Unsupported flag: " + flag);
    }
}
