package com.rustret.rg.objects;

import cn.nukkit.level.Position;

public class Selection {
    private Position pos1, pos2;
    private int xLength, yLength, zLength;

//    public SelectionResponse update(Position pos) {
//        if (pos1 == null) {
//            pos1 = pos;
//            return SelectionResponse.POS1;
//        }
//        else if (pos2 == null) {
//            pos2 = pos;
//            return SelectionResponse.POS2;
//        }
//        else {
//            pos1 = pos;
//            pos2 = null;
//            return SelectionResponse.POS1;
//        }
//    }

    public boolean hasTwoPoints() {
        return pos1 != null && pos2 != null;
    }

    public boolean levelsMatch() {
        return pos1.getLevel().getId() == pos2.getLevel().getId();
    }

    public void align() {
        int minX = (int) Math.min(pos1.x, pos2.x);
        int maxX = (int) Math.max(pos1.x, pos2.x);
        int minY = (int) Math.min(pos1.y, pos2.y);
        int maxY = (int) Math.max(pos1.y, pos2.y);
        int minZ = (int) Math.min(pos1.z, pos2.z);
        int maxZ = (int) Math.max(pos1.z, pos2.z);

        pos1.x = minX;
        pos2.x = maxX;
        pos1.y = minY;
        pos2.y = maxY;
        pos1.z = minZ;
        pos2.z = maxZ;
    }

    public boolean hasSideBiggerThan(int value) {
        xLength = (int) (pos2.x - pos1.x + 1);
        yLength = (int) (pos2.y - pos1.y + 1);
        zLength = (int) (pos2.z - pos1.z + 1);

        return xLength > value || yLength > value || zLength > value;
    }

    public int getSize() {
        return xLength * yLength * zLength;
    }
}
