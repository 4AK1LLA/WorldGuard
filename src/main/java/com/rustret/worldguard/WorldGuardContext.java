package com.rustret.worldguard;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
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
import java.util.stream.Collectors;


public class WorldGuardContext {
    private final String connectionString;
    private final Map<Long, Position[]> selections = new HashMap<>();
    private final Map<String, AtomicReference<Region>> regions = new HashMap<>();
    private final Map<UUID, List<AtomicReference<Region>>> playerRegions = new HashMap<>();
    private final RegionRTree rtree = new RegionRTree(Server.getInstance().getLevels().keySet());

    WorldGuardContext() {
        connectionString = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s",
                PluginConfig.getHost(), PluginConfig.getPort(), PluginConfig.getDatabase(),
                PluginConfig.getUsername(), PluginConfig.getPassword());

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
                UUID ownerId = model.getOwnerId();
                int levelId = model.getLevelId();

                Region region = new Region(regionName, model.getOwnerName(), ownerId, coordinates, levelId, model.getMemberIds(), model.getPvp());
                AtomicReference<Region> reference = new AtomicReference<>(region);

                regions.put(regionName, reference);
                rtree.put(regionName, coordinates, levelId);
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
                        region.ownerId,
                        region.coordinates,
                        region.levelId,
                        region.memberIds,
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

    public Position[] getSelection(long playerId) {
        return selections.get(playerId);
    }

    public void setSelection(long playerId, Position[] selection) {
        selections.put(playerId, selection);
    }

    public void removeSelection(long playerId) {
        selections.remove(playerId);
    }

    public void addRegion(String regionName, String ownerName, UUID ownerId, Vector3[] coordinates, int levelId) {
        Region region = new Region(regionName, ownerName, ownerId, coordinates, levelId);
        AtomicReference<Region> reference = new AtomicReference<>(region);
        regions.put(regionName, reference);
        rtree.put(regionName, coordinates, levelId);
        playerRegions.computeIfAbsent(ownerId, k -> new ArrayList<>()).add(reference);
    }

    public void removeRegion(String regionName) {
        AtomicReference<Region> reference = regions.remove(regionName);
        Region region = reference.get();
        rtree.remove(regionName, region.coordinates, region.levelId);
        playerRegions.get(region.ownerId).removeIf(ref -> ref.get() == region);
    }

    public String findRtreeRegionName(Vector3 point, int levelId) {
        return rtree.findRegionName(point, levelId);
    }

    public UUID getRegionOwnerId(String regionName) {
        return getRegion(regionName).ownerId;
    }

    public List<UUID> getAllowedIds(String regionName) {
        Region region = getRegion(regionName);
        List<UUID> result = new ArrayList<>(region.memberIds);
        result.add(region.ownerId);

        return result;
    }

    public boolean intersectsRegion(Vector3[] coordinates, int levelId) {
        return rtree.intersects(coordinates, levelId);
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
        Region region = getRegion(regionName);
        region.setFlag(flag, value);
    }

    public boolean getFlagValue(String regionName, String flag) {
        Region region = getRegion(regionName);
        return region.getFlag(flag);
    }

    public int getRegionMembersCount(String regionName) {
        Region region = getRegion(regionName);
        return region.memberIds.size();
    }

    public UUID findPlayerId(String name) {
        Player player = Server.getInstance().getPlayerExact(name);
        return (player != null) ? player.getUniqueId() : null;
    }

    public boolean memberExists(String regionName, UUID memberId) {
        Region region = getRegion(regionName);
        return region.memberIds.contains(memberId);
    }

    public void addMember(String regionName, UUID memberId) {
        Region region = getRegion(regionName);
        region.memberIds.add(memberId);
    }

    public boolean removeMember(String regionName, String memberName) {
        Region region = getRegion(regionName);
        for (UUID memberId: region.memberIds) {
            IPlayer player = Server.getInstance().getOfflinePlayer(memberId);
            if (Objects.equals(player.getName(), memberName)) {
                region.memberIds.remove(memberId);
                return true;
            }
        }

        return false;
    }

    public List<String> getPlayerRegionNames(UUID playerId) {
        List<AtomicReference<Region>> regionRefs = playerRegions.get(playerId);
        if (regionRefs == null) return Collections.emptyList();

        return regionRefs
                .stream()
                .map(AtomicReference::get)
                .map(region -> region.regionName)
                .collect(Collectors.toList());
    }

    public Region getRegion(String regionName) {
        return regions.get(regionName).get();
    }
}
