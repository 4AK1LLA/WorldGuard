package com.rustret.worldguard.rtree;

import cn.nukkit.math.Vector3;
import com.github.davidmoten.rtreemulti.Entry;
import com.github.davidmoten.rtreemulti.RTree;
import com.github.davidmoten.rtreemulti.geometry.Point;
import com.github.davidmoten.rtreemulti.geometry.Rectangle;

import java.util.*;

public class RegionRTree {
    private final Map<Integer, RTree<String, Rectangle>> map = new HashMap<>();

    public RegionRTree(Set<Integer> levelIds) {
        for (int levelId: levelIds) {
            map.put(levelId, RTree.dimensions(3).create());
        }
    }

    public void put(String regionName, Vector3[] coordinates, int levelId) {
        RTree<String, Rectangle> rtree = map.get(levelId);
        rtree = rtree.add(regionName, createRectangle(coordinates));
        map.put(levelId, rtree);
    }

    public void remove(String regionName, Vector3[] coordinates, int levelId) {
        RTree<String, Rectangle> rtree = map.get(levelId);
        rtree = rtree.delete(regionName, createRectangle(coordinates));
        map.put(levelId, rtree);
    }

    public boolean intersects(Vector3 point, int levelId) {
        RTree<String, Rectangle> rtree = map.get(levelId);
        return rtree.search(Point.create(point.x, point.y, point.z)).iterator().hasNext();
    }

    public boolean intersects(Vector3[] coordinates, int levelId) {
        RTree<String, Rectangle> rtree = map.get(levelId);
        return rtree.search(createRectangle(coordinates)).iterator().hasNext();
    }

    public String findRegionName(Vector3 point, int levelId) {
        RTree<String, Rectangle> rtree = map.get(levelId);
        Iterator<Entry<String, Rectangle>> result = rtree.search(Point.create(point.x, point.y, point.z)).iterator();

        return result.hasNext() ? result.next().value() : null;
    }

    public String asString(int levelId) {
        return map.get(levelId).asString();
    }

    private Rectangle createRectangle(Vector3[] coordinates) {
        double minX = Math.min(coordinates[0].x, coordinates[1].x);
        double minY = Math.min(coordinates[0].y, coordinates[1].y);
        double minZ = Math.min(coordinates[0].z, coordinates[1].z);

        double maxX = Math.max(coordinates[0].x, coordinates[1].x);
        double maxY = Math.max(coordinates[0].y, coordinates[1].y);
        double maxZ = Math.max(coordinates[0].z, coordinates[1].z);

        return Rectangle.create(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
