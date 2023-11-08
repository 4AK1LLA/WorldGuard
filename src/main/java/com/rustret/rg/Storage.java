package com.rustret.rg;

import cn.nukkit.math.Vector3;
import com.github.davidmoten.rtreemulti.Entry;
import com.github.davidmoten.rtreemulti.RTree;
import com.github.davidmoten.rtreemulti.geometry.Rectangle;
import com.rustret.rg.objects.Region;
import com.rustret.rg.objects.Selection;

import java.util.*;

public class Storage {
    public final Map<UUID, Selection> selections = new HashMap<>();
    public final Map<String, Region> regions = new HashMap<>();
    public final Map<UUID, List<Region>> players = new HashMap<>();
    public RTree<Region, Rectangle> rtree = RTree.create(3);

    public void removeSelection(UUID id) {
        selections.remove(id);
    }

    public int getNumberOfRegions(UUID id) {
        return players.getOrDefault(id, Collections.emptyList()).size();
    }

    /**
     * Creates region if its coordinates do not intersect other regions
     * Appends the specified element to the end of this list (optional
     * operation).
     *
     * @return <tt>true</tt> if region was created and added to regions,
     * players and rtree.
     * <p><tt>false</tt> if region was not created because of intersecting
     * with another region.
     */
    public boolean checkIntersectAndAdd(String name, UUID id, Selection s) {
        Rectangle rectangle = mapRectangle(s.pos1, s.pos2);
        int level = s.pos1.level.getId();

        Iterable<Entry<Region, Rectangle>>  results = rtree.search(rectangle);
        for (Entry<Region, Rectangle> entry : results) {
            if (entry.value().level == level) {
                return false;
            }
        }

        Region region = new Region(name, id, s.pos1, s.pos2, level);
        regions.put(name, region);
        players.computeIfAbsent(region.owner, k -> new ArrayList<>())
                .add(region);
        rtree = rtree.add(region, rectangle);
        return true;
    }

    private Rectangle mapRectangle(Vector3 min, Vector3 max) {
        return Rectangle.create(min.x, min.y, min.z, max.x, max.y, max.z);
    }
}
