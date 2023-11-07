package com.rustret.rg.selection;

import cn.nukkit.level.Position;
import com.rustret.rg.selection.objects.Selection;
import com.rustret.rg.selection.objects.SelectionResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SelectionManager {
    private final Map<UUID, Selection> selections = new HashMap<>();

    public Selection get(UUID id) {
        return selections.get(id);
    }

    public void remove(UUID id) {
        selections.remove(id);
    }

    public SelectionResponse put(UUID id, Position pos) {
        selections.putIfAbsent(id, new Selection());
        return selections.get(id).update(pos);
    }
}
