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
    private RTree<String, Rectangle> rtree;

    WorldGuardContext(Config config) {
        rtree = RTree.dimensions(3).create();

        //x1 y1 z1 x2 y2 z2
        rtree = rtree.add("test", Rectangle.create(-1022, 104, -1953, -1018, 107, -1948));

        Iterable<Entry<String, Rectangle>> result = rtree.search(Point.create(-1022, 104, -1953));

        for (Entry<String, Rectangle> e: result) {
            System.out.println(e.value());
            System.out.println(e.geometry());
        }

        playerSelections = new HashMap<>();

        connectionString = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s",
                config.get("MySql.host"), (int)config.get("MySql.port"), config.get("MySql.database"),
                config.get("MySql.username"), config.get("MySql.password"));

        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(connectionString);
            TableUtils.createTableIfNotExists(connectionSource, RegionModel.class);

            connectionSource.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public boolean addRegion(RegionModel region) {
        boolean result = false;

        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(connectionString);
            Dao<RegionModel, Integer> dao = DaoManager.createDao(connectionSource, RegionModel.class);

            result = dao.create(region) == 1;

            connectionSource.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean removeRegion(String regionName, Player player) {
        boolean result = false;

        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(connectionString);
            Dao<RegionModel, Integer> dao = DaoManager.createDao(connectionSource, RegionModel.class);

            RegionModel region = dao.queryForFirst(
                    dao
                    .queryBuilder()
                    .where()
                    .eq("regionName", regionName)
                    .prepare());

            if (region == null) {
                Messages.RG_NOT_EXIST.send(player, regionName);
                connectionSource.close();
                return false;
            }

            boolean ownerIsValid = region.getOwnerId().equals(player.getUniqueId().toString());
            if (!ownerIsValid && !player.hasPermission("worldguard.god")) {
                Messages.RG_NOT_OWNER.send(player);
                connectionSource.close();
                return false;
            }

            result = dao.delete(region) == 1;

            connectionSource.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean blockIsPrivated(Block block) {
        int x = (int)block.getX();
        int y = (int)block.getY();
        int z = (int)block.getZ();

        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(connectionString);
            Dao<RegionModel, Integer> dao = DaoManager.createDao(connectionSource, RegionModel.class);

            List<RegionModel> regions = dao.queryForAll();

            for (RegionModel rg : regions) {
                Coord pos1 = rg.getPos1();
                Coord pos2 = rg.getPos2();

                int minX = Math.min(pos1.x, pos2.x);
                int maxX = Math.max(pos1.x, pos2.x);
                int minY = Math.min(pos1.y, pos2.y);
                int maxY = Math.max(pos1.y, pos2.y);
                int minZ = Math.min(pos1.z, pos2.z);
                int maxZ = Math.max(pos1.z, pos2.z);

                if (x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ) {
                    connectionSource.close();
                    return true;
                }
            }

            connectionSource.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
