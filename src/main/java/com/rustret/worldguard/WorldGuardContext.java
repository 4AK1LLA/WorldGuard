package com.rustret.worldguard;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;
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
import com.rustret.worldguard.entities.Coord;
import com.rustret.worldguard.entities.CoordPair;
import com.rustret.worldguard.dbmodels.RegionModel;
import com.rustret.worldguard.entities.Region;

import java.util.*;


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

        loadDatabase();
    }

    public void loadDatabase() {
        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(connectionString);
            TableUtils.createTableIfNotExists(connectionSource, RegionModel.class);

            Dao<RegionModel, Integer> dao = DaoManager.createDao(connectionSource, RegionModel.class);
            List<RegionModel> models = dao.queryForAll();

            connectionSource.close();

            if (models.size() == 0) {
                return;
            }

            for (RegionModel model: models) {
                String regionName = model.getRegionName();
                Coord pos1 = model.getPos1();
                Coord pos2 = model.getPos2();

                Region region = new Region(regionName, model.getOwnerName(), UUID.fromString(model.getOwnerId()), pos1, pos2);
                regions.put(regionName, region);
                rtree = rtree.add(regionName, createRectangle(pos1, pos2));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveDatabase() {
        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(connectionString);
            TableUtils.clearTable(connectionSource, RegionModel.class);
            Dao<RegionModel, Integer> dao = DaoManager.createDao(connectionSource, RegionModel.class);

            if (regions.size() == 0) {
                return;
            }

            List<RegionModel> models = new ArrayList<>();
            for (Region region: regions.values()) {
                RegionModel model = new RegionModel(
                        region.regionName,
                        region.ownerName,
                        region.ownerId.toString(),
                        region.pos1,
                        region.pos2,
                        region.pvp
                );
                models.add(model);
            }

            dao.create(models);
        }
        catch (Exception e) {
            e.printStackTrace();
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
        regions.put(regionName, region);
        rtree = rtree.add(regionName, createRectangle(region.pos1, region.pos2));

        return true;
    }

    public Region getRegion(String regionName) {
        return regions.get(regionName);
    }

    public boolean removeRegion(String regionName) {
        Region removedRg = regions.remove(regionName);
        rtree = rtree.delete(regionName, createRectangle(removedRg.pos1, removedRg.pos2));

        return true;
    }

    public boolean canInteract(Vector3 pos, Player player) {
        Iterable<Entry<String, Rectangle>> result = rtree.search(Point.create(pos.x, pos.y, pos.z));

        if (!result.iterator().hasNext()) {
            return true;
        }

        String regionName = result.iterator().next().value();
        Region region = regions.get(regionName);

        return region.ownerId.equals(player.getUniqueId()) || player.hasPermission("worldguard.god");
    }

    public boolean intersectsRegion(CoordPair selection) {
        Iterable<Entry<String, Rectangle>> result = rtree.search(createRectangle(selection.pos1, selection.pos2));

        return result.iterator().hasNext();
    }

    public int getRegionsCount() {
        return regions.size();
    }

    public long getPlayerRegionsCount(UUID playerId) {
        return regions.values()
                .stream()
                .filter(region -> region.ownerId.equals(playerId))
                .count();
    }

    public boolean regionExists(String regionName) {
        return getRegion(regionName) != null;
    }

    public void updateFlag(String regionName, String flag, boolean value) {
        Region region = getRegion(regionName);
        region.setFlag(flag, value);
    }
}
