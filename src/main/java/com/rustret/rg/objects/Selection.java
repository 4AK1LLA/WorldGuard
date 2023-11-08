package com.rustret.rg.objects;

import cn.nukkit.level.Position;

public class Selection {
    public Position pos1, pos2;

    public void align() {
        int minX = (int) Math.min(pos1.x, pos2.x);
        int maxX = (int) Math.max(pos1.x, pos2.x);
        int minY = (int) Math.min(pos1.y, pos2.y);
        int maxY = (int) Math.max(pos1.y, pos2.y);
        int minZ = (int) Math.min(pos1.z, pos2.z);
        int maxZ = (int) Math.max(pos1.z, pos2.z);

        pos1.setComponents(minX, minY, minZ);
        pos2.setComponents(maxX, maxY, maxZ);
    }
}
