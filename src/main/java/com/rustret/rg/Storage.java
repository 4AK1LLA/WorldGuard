package com.rustret.rg;

import com.rustret.rg.objects.Region;
import com.rustret.rg.objects.Selection;

import java.util.*;

public class Storage {
    private final Map<UUID, Selection> selections = new HashMap<>();
    private final Map<String, Region> regions = new HashMap<>();
    private final Map<UUID, List<Region>> players = new HashMap<>();

    public Storage() {
    }
}
