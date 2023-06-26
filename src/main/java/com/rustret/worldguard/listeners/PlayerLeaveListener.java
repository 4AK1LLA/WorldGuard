package com.rustret.worldguard.listeners;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import com.rustret.worldguard.WorldGuardContext;

public class PlayerLeaveListener implements Listener {
    private final WorldGuardContext context;

    public PlayerLeaveListener(WorldGuardContext context) {
        this.context = context;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        context.removeSelection(event.getPlayer().getId());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        context.removeSelection(event.getPlayer().getId());
    }
}
