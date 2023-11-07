package com.rustret.rg.objects;

import cn.nukkit.math.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Region {
    public final String name;
    public final UUID owner;
    public final Vector3 min, max;
    public final int level;
    public List<UUID> members;
    public boolean pvp, secret;

    public Region(String name, UUID owner, Vector3 min, Vector3 max, int level) {
        this.name = name;
        this.owner = owner;
        this.min = min;
        this.max = max;
        this.level = level;

        this.members = new ArrayList<>();
        this.pvp = true;
        this.secret = false;
    }
}
