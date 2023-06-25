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
import com.rustret.worldguard.dbmodels.RegionModel;
import com.rustret.worldguard.entities.Region;
import com.rustret.worldguard.rtree.RegionRTree;

import java.util.*;


public class WorldGuardContext {
    private final String connectionString;
    private final Map<Long, Vector3[]> selections = new HashMap<>();
    private final Map<String, Region> regions = new HashMap<>();
    private RegionRTree rtree = new RegionRTree();

    WorldGuardContext(Config config) {
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
                Vector3[] coordinates = model.getCoordinates();

                Region region = new Region(regionName, model.getOwnerName(), UUID.fromString(model.getOwnerId()), coordinates);
                regions.put(regionName, region);
                rtree.put(regionName, coordinates);
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
                        region.coordinates,
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

    public Vector3[] getSelection(Player player) {
        return selections.get(player.getId());
    }

    public void setSelection(Player player, Vector3 point) {
        long playerId = player.getId();

        Vector3[] selection = selections.get(playerId);

        //Init selection and pos1
        if (selection == null) {
            selection = new Vector3[] { point, null };

            Messages.FIRST_POS.send(player, (int) point.x, (int) point.y, (int) point.z);
        }

        //Init pos2
        else if (selection[1] == null) {
            selection[1] = point;

            Messages.SECOND_POS.send(player, (int) point.x, (int) point.y, (int) point.z);
        }

        //Reset selection and init pos1
        else if (selection[0] != null) {
            selection[0] = point;
            selection[1] = null;

            Messages.FIRST_POS.send(player, (int) point.x, (int) point.y, (int) point.z);
        }

        selections.put(playerId, selection);
    }

    public void removePlayerSelection(Player player) {
        selections.remove(player.getId());
    }

    public boolean addRegion(String regionName, Region region) {
        regions.put(regionName, region);
        rtree.put(regionName, region.coordinates);

        return true;
    }

    public Region getRegion(String regionName) {
        return regions.get(regionName);
    }

    public boolean removeRegion(String regionName) {
        Region removedRg = regions.remove(regionName);
        rtree.remove(regionName, removedRg.coordinates);

        return true;
    }

    public boolean canInteract(Vector3 point, Player player) {
        String regionName = rtree.findRegionName(point);
        Region region = regions.get(regionName);

        return region.ownerId.equals(player.getUniqueId()) || player.hasPermission("worldguard.god");
    }

    public boolean intersectsRegion(Vector3[] coordinates) {
        return rtree.intersects(coordinates);
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
