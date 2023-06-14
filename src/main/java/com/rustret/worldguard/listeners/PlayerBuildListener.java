package com.rustret.worldguard.listeners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import com.rustret.worldguard.Messages;
import com.rustret.worldguard.WorldGuardContext;

public class PlayerBuildListener implements Listener {
    private final WorldGuardContext context;

    public PlayerBuildListener(WorldGuardContext context) {
        this.context = context;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (context.canBuild(block, player)) {
            return;
        }

        event.setCancelled();
        Messages.FOREIGN_RG.send(player);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (context.canBuild(block, player)) {
            return;
        }

        event.setCancelled();
        Messages.FOREIGN_RG.send(player);
    }
}
