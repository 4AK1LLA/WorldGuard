package com.rustret.rg.objects;

import cn.nukkit.math.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Region {
    public String name;
    public UUID owner;
    public Vector3 min, max;
    public int level;
    public List<UUID> members;
    public boolean pvp, secret, mobs;

    public Region(String name, UUID owner, Vector3 min, Vector3 max, int level) {
        this.name = name;
        this.owner = owner;
        this.min = min;
        this.max = max;
        this.level = level;

        this.members = new ArrayList<>();
        this.pvp = true;
        this.secret = false;
        this.mobs = true;
    }

    public Region(String name, UUID owner, Vector3 min, Vector3 max, int level, List<UUID> members, boolean pvp, boolean secret, boolean mobs) {
        this(name, owner, min, max, level);

        this.members = members;
        this.pvp = pvp;
        this.secret = secret;
        this.mobs = mobs;
    }
}
