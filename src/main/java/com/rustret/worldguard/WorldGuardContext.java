package com.rustret.worldguard;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.rustret.worldguard.coordinates.Coord;
import com.rustret.worldguard.coordinates.CoordPair;
import com.rustret.worldguard.dbmodels.RegionModel;

import java.util.HashMap;
import java.util.Map;

public class WorldGuardContext {
    private final String connectionString;
    private final Map<Long, CoordPair> playerSelections;

    WorldGuardContext(Config config) {
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

        //REMOVE
        for (long key: playerSelections.keySet()) {
            String p1 = (playerSelections.get(key).pos1 == null) ? "null" : playerSelections.get(key).pos1.toString();
            String p2 = (playerSelections.get(key).pos2 == null) ? "null" : playerSelections.get(key).pos2.toString();
            System.out.println("Key: " + key + " Coordinates: " + p1 + " | " + p2);
        }
    }

    public void removePlayerSelection(Player player) {
        playerSelections.remove(player.getId());
    }

    public int addRegion(RegionModel region) {
        int result = 0;

        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(connectionString);
            Dao<RegionModel, Integer> dao = DaoManager.createDao(connectionSource, RegionModel.class);

            result = dao.create(region);

            connectionSource.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
