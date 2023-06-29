package com.rustret.worldguard;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.rustret.worldguard.dbmodels.RegionModel;
import com.rustret.worldguard.entities.Region;
import com.rustret.worldguard.rtree.RegionRTree;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


public class WorldGuardContext {
    private final String connectionString;
    private final Map<Long, Vector3[]> selections = new HashMap<>();
    private final Map<String, AtomicReference<Region>> regions = new HashMap<>();
    private final Map<UUID, List<AtomicReference<Region>>> playerRegions = new HashMap<>();
    private final RegionRTree rtree = new RegionRTree();

    WorldGuardContext(Config config) {
        connectionString = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s",
                config.get("MySql.host"), (int)config.get("MySql.port"), config.get("MySql.database"),
                config.get("MySql.username"), config.get("MySql.password"));

        loadDatabase();
    }

    public void loadDatabase() {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(connectionString)) {
            TableUtils.createTableIfNotExists(connectionSource, RegionModel.class);

            Dao<RegionModel, Integer> dao = DaoManager.createDao(connectionSource, RegionModel.class);
            List<RegionModel> models = dao.queryForAll();

            if (models.isEmpty()) return;

            for (RegionModel model: models) {
                String regionName = model.getRegionName();
                Vector3[] coordinates = model.getCoordinates();
                UUID ownerId = UUID.fromString(model.getOwnerId());

                Region region = new Region(regionName, model.getOwnerName(), ownerId, coordinates, null); //Add to DB
                AtomicReference<Region> reference = new AtomicReference<>(region);

                regions.put(regionName, reference);
                rtree.put(regionName, coordinates);
                playerRegions.computeIfAbsent(ownerId, k -> new ArrayList<>()).add(reference);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveDatabase() {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(connectionString)) {
            TableUtils.clearTable(connectionSource, RegionModel.class);
            Dao<RegionModel, Integer> dao = DaoManager.createDao(connectionSource, RegionModel.class);

            if (regions.isEmpty()) return;

            List<RegionModel> models = new ArrayList<>();
            for (AtomicReference<Region> reference: regions.values()) {
                Region region = reference.get();
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

    public Vector3[] getSelection(long playerId) {
        return selections.get(playerId);
    }

    public void setSelection(long playerId, Vector3[] selection) {
        selections.put(playerId, selection);
    }

    public void removeSelection(long playerId) {
        selections.remove(playerId);
    }

    public void addRegion(String regionName, String ownerName, UUID ownerId, Vector3[] coordinates) {
        Region region = new Region(regionName, ownerName, ownerId, coordinates, null);
        AtomicReference<Region> reference = new AtomicReference<>(region);
        regions.put(regionName, reference);
        rtree.put(regionName, coordinates);
        playerRegions.computeIfAbsent(ownerId, k -> new ArrayList<>()).add(reference);
    }

    public void removeRegion(String regionName) {
        AtomicReference<Region> reference = regions.remove(regionName);
        Region region = reference.get();
        rtree.remove(regionName, region.coordinates);
        playerRegions.get(region.ownerId).removeIf(ref -> ref.get() == region);
    }

    public String findRtreeRegionName(Vector3 point) {
        return rtree.findRegionName(point);
    }

    public UUID getRegionOwnerId(String regionName) {
        return regions.get(regionName).get().ownerId;
    }

    public boolean intersectsRegion(Vector3[] coordinates) {
        return rtree.intersects(coordinates);
    }

    public int getRegionsCount() {
        return regions.size();
    }

    public int getPlayerRegionsCount(UUID playerId) {
        return playerRegions.getOrDefault(playerId, Collections.emptyList()).size();
    }

    public boolean regionExists(String regionName) {
        return regions.containsKey(regionName);
    }

    public void updateFlag(String regionName, String flag, boolean value) {
        Region region = regions.get(regionName).get();
        region.setFlag(flag, value);
    }

    public boolean getFlagValue(String regionName, String flag) {
        Region region = regions.get(regionName).get();
        return region.getFlag(flag);
    }

    public int getRegionMembersCount(String regionName) {
        Region region = regions.get(regionName).get();
        return region.memberIds != null ? region.memberIds.size() : 0;
    }

    public UUID findPlayerId(String name) {
        Player player = Server.getInstance().getPlayerExact(name);
        return (player != null) ? player.getUniqueId() : null;
    }

    public boolean memberExists(String regionName, UUID memberId) {
        Region region = regions.get(regionName).get();
        return memberId.equals(region.ownerId) || (region.memberIds != null && region.memberIds.contains(memberId));
    }

    public void addMember(String regionName, UUID memberId) {
        Region region = regions.get(regionName).get();
        if (region.memberIds == null) {
            region.memberIds = new ArrayList<>();
        }
        region.memberIds.add(memberId);
    }
}
