package com.rustret.worldguard;

import cn.nukkit.Player;

import java.util.HashMap;
import java.util.Map;

public class WorldGuardContext {
    private static final Map<Long, CoordPair> playerSelections = new HashMap<>();

    public static void setPos(Player player) {
        long playerId = player.getId();

        CoordPair selection = playerSelections.get(playerId);

        System.out.print(selection);

        if (selection == null) {
            CoordPair newSelection = new CoordPair();
            playerSelections.put(playerId, newSelection);

            System.out.print(playerSelections.get(playerId));
        }

        System.out.print("\n");
    }
}
