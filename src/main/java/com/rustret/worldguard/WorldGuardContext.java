package com.rustret.worldguard;

import cn.nukkit.Player;
import com.rustret.worldguard.coordinates.Coord;
import com.rustret.worldguard.coordinates.CoordPair;

import java.util.HashMap;
import java.util.Map;

public class WorldGuardContext {
    private static final Map<Long, CoordPair> playerSelections = new HashMap<>();

    public static CoordPair getPlayerSelection(Player player) {
        return playerSelections.get(player.getId());
    }

    public static void setPlayerSelection(Player player, Coord incomeCoords) {
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

    public static void removePlayerSelection(Player player) {
        playerSelections.remove(player.getId());
    }
}
