package com.rustret.worldguard.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerTeleportEvent;
import com.rustret.worldguard.WorldGuardContext;

public class PlayerTeleportListener implements Listener {
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        WorldGuardContext.removePlayerSelection(player);
    }
}
