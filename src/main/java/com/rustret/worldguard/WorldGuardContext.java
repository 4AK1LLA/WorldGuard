package com.rustret.worldguard;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.utils.Config;
import com.github.davidmoten.rtreemulti.Entry;
import com.github.davidmoten.rtreemulti.RTree;
import com.github.davidmoten.rtreemulti.geometry.Point;
import com.github.davidmoten.rtreemulti.geometry.Rectangle;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.rustret.worldguard.coordinates.Coord;
import com.rustret.worldguard.coordinates.CoordPair;
import com.rustret.worldguard.dbmodels.RegionModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldGuardContext {
    private final String connectionString;
    private final Map<Long, CoordPair> playerSelections;
    private final Map<String, Region> regions;
    private RTree<String, Rectangle> rtree;

    WorldGuardContext(Config config) {
        playerSelections = new HashMap<>();
        regions = new HashMap<>();
        rtree = RTree.dimensions(3).create();
        connectionString = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s",
                config.get("MySql.host"), (int)config.get("MySql.port"), config.get("MySql.database"),
                config.get("MySql.username"), config.get("MySql.password"));

        List<RegionModel> dbRegions = null;
        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(connectionString);
            TableUtils.createTableIfNotExists(connectionSource, RegionModel.class);

            Dao<RegionModel, Integer> dao = DaoManager.createDao(connectionSource, RegionModel.class);
            dbRegions = dao.queryForAll();

            connectionSource.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (dbRegions == null || dbRegions.size() == 0) {
            return;
        }

        for (RegionModel model: dbRegions) {
            String regionName = model.getRegionName();
            Coord pos1 = model.getPos1();
            Coord pos2 = model.getPos2();

            Region region = new Region(model.getOwnerName(), model.getOwnerId(), pos1, pos2);
            regions.put(regionName, region);

            rtree = rtree.add(regionName, createRectangle(pos1, pos2));
        }
    }

    private Rectangle createRectangle(Coord pos1, Coord pos2) {
        int minX = Math.min(pos1.x, pos2.x);
        int minY = Math.min(pos1.y, pos2.y);
        int minZ = Math.min(pos1.z, pos2.z);

        int maxX = Math.max(pos1.x, pos2.x);
        int maxY = Math.max(pos1.y, pos2.y);
        int maxZ = Math.max(pos1.z, pos2.z);

        return Rectangle.create(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public CoordPair getPlayerSelection(Player player) {
        return playerSelections.get(player.getId());
    }

    public void setPlayerSelection(Player player, Coord incomeCoords) {
        long playerId = player.getId();

        CoordPair selection = playerSelections.get(playerId);

        //Init selection and pos1
        if (selection == null) {
            selection = new CoordPair();
            selection.pos1 = incomeCoords;

            Messages.FIRST_POS.send(player, incomeCoords.x, incomeCoords.y, incomeCoords.z);
        }

        //Init pos2
        else if (selection.pos2 == null) {
            selection.pos2 = incomeCoords;

            Messages.SECOND_POS.send(player, incomeCoords.x, incomeCoords.y, incomeCoords.z);
        }

        //Reset selection and init pos1
        else if (selection.pos1 != null) {
            selection.pos1 = incomeCoords;
            selection.pos2 = null;

            Messages.FIRST_POS.send(player, incomeCoords.x, incomeCoords.y, incomeCoords.z);
        }

        playerSelections.put(playerId, selection);
    }

    public void removePlayerSelection(Player player) {
        playerSelections.remove(player.getId());
    }

    public boolean addRegion(String regionName, Region region) {
        boolean success = false;
        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(connectionString);
            Dao<RegionModel, Integer> dao = DaoManager.createDao(connectionSource, RegionModel.class);

            RegionModel model = new RegionModel(regionName, region.ownerName, region.ownerId, region.pos1, region.pos2);
            success = dao.create(model) == 1;

            connectionSource.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (!success) {
            return false;
        }

        regions.put(regionName, region);
        rtree = rtree.add(regionName, createRectangle(region.pos1, region.pos2));

        return true;
    }

    public Region getRegion(String regionName) {
        return regions.get(regionName);
    }

    public boolean removeRegion(String regionName) {
        boolean success = false;
        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(connectionString);
            Dao<RegionModel, Integer> dao = DaoManager.createDao(connectionSource, RegionModel.class);

            RegionModel model = dao.queryForFirst(
                    dao
                    .queryBuilder()
                    .where()
                    .eq("regionName", regionName)
                    .prepare());

            success = dao.delete(model) == 1;

            connectionSource.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (!success) {
            return false;
        }

        Region removedRg = regions.remove(regionName);
        rtree = rtree.delete(regionName, createRectangle(removedRg.pos1, removedRg.pos2));

        return true;
    }

    public boolean canBuild(Block block, Player player) {
        Iterable<Entry<String, Rectangle>> result = rtree.search(Point.create((int)block.getX(), (int)block.getY(), (int)block.getZ()));

        if (!result.iterator().hasNext()) {
            return true;
        }

        String regionName = result.iterator().next().value();
        Region region = regions.get(regionName);

        return region.ownerId.equals(player.getUniqueId().toString()) || player.hasPermission("worldguard.god");
    }

    public boolean intersectsRegion(CoordPair selection) {
        Iterable<Entry<String, Rectangle>> result = rtree.search(createRectangle(selection.pos1, selection.pos2));

        return result.iterator().hasNext();
    }

    public int getRegionsCount() {
        return regions.size();
    }
}
