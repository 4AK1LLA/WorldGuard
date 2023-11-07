package com.rustret.rg.selection.objects;

import cn.nukkit.level.Position;

public class Selection {
    public Position pos1, pos2;

    public SelectionResponse update(Position pos) {
        if (pos1 == null) {
            pos1 = pos;
            return SelectionResponse.POS1;
        }
        else if (pos2 == null) {
            pos2 = pos;
            return SelectionResponse.POS2;
        }
        else {
            pos1 = pos;
            pos2 = null;
            return SelectionResponse.POS1;
        }
    }
}
