package com.rustret.worldguard.rtree;

import cn.nukkit.math.Vector3;
import com.github.davidmoten.rtreemulti.Entry;
import com.github.davidmoten.rtreemulti.RTree;
import com.github.davidmoten.rtreemulti.geometry.Point;
import com.github.davidmoten.rtreemulti.geometry.Rectangle;

import java.util.Iterator;

public class RegionRTree {
    private RTree<String, Rectangle> rtree = RTree.dimensions(3).create();

    public void put(String regionName, Vector3[] coordinates) {
        rtree = rtree.add(regionName, createRectangle(coordinates));
    }

    public void remove(String regionName, Vector3[] coordinates) {
        rtree = rtree.delete(regionName, createRectangle(coordinates));
    }

    public boolean intersects(Vector3 point) {
        return rtree.search(Point.create(point.x, point.y, point.z)).iterator().hasNext();
    }

    public boolean intersects(Vector3[] coordinates) {
        return rtree.search(createRectangle(coordinates)).iterator().hasNext();
    }

    public String findRegionName(Vector3 point) {
        Iterator<Entry<String, Rectangle>> result = rtree.search(Point.create(point.x, point.y, point.z)).iterator();

        return result.hasNext() ? result.next().value() : null;
    }

    public String asString() {
        return rtree.asString();
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
