package com.rustret.worldguard;

import cn.nukkit.Player;

import java.util.HashMap;
import java.util.Map;

public class WorldGuardContext {
    private static final Map<Long, CoordPair> playerSelections = new HashMap<>();

    public static void setPlayerSelection(Player player, Coord incomeCoords) {
        long playerId = player.getId();

        CoordPair selection = playerSelections.get(playerId);

        //Init selection and pos1
        if (selection == null) {
            selection = new CoordPair();
            selection.pos1 = incomeCoords;

            Messages.FIRST_POS.send(player, incomeCoords.toString());
        }

        //Init pos2
        else if (selection.pos2 == null) {
            selection.pos2 = incomeCoords;

            Messages.SECOND_POS.send(player, incomeCoords.toString());
        }

        //Reset selection and init pos1
        else if (selection.pos1 != null) {
            selection.pos1 = incomeCoords;
            selection.pos2 = null;

            Messages.FIRST_POS.send(player, incomeCoords.toString());
        }

        playerSelections.put(playerId, selection);

        for (long key: playerSelections.keySet()) {
            String p1 = (playerSelections.get(key).pos1 == null) ? "null" : playerSelections.get(key).pos1.toString();
            String p2 = (playerSelections.get(key).pos2 == null) ? "null" : playerSelections.get(key).pos2.toString();
            System.out.println("Key: " + key + " Coordinates: " + p1 + " | " + p2);
        }
    }

    public static void removePlayerSelection(Player player) {
        CoordPair p = playerSelections.remove(player.getId());
        System.out.println(p);
    }
}
