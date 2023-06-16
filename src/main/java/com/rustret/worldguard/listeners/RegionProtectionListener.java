package com.rustret.worldguard.listeners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerBucketEmptyEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import com.rustret.worldguard.Messages;
import com.rustret.worldguard.WorldGuardContext;

public class RegionProtectionListener implements Listener {
    private final WorldGuardContext context;

    public RegionProtectionListener(WorldGuardContext context) {
        this.context = context;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (context.canInteract(block, player)) {
            return;
        }

        event.setCancelled();
        Messages.FOREIGN_RG.sendPopup(player);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (context.canInteract(block, player)) {
            return;
        }

        event.setCancelled();
        Messages.FOREIGN_RG.sendPopup(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) ||
                event.getAction().equals(PlayerInteractEvent.Action.LEFT_CLICK_BLOCK)) {
            Block block = event.getBlock();
            Player player = event.getPlayer();

            if (context.canInteract(block, player)) {
                return;
            }

            event.setCancelled();
            Messages.FOREIGN_RG.sendPopup(player);
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Block block = event.getBlockClicked();
        Player player = event.getPlayer();

        if (context.canInteract(block, player)) {
            return;
        }

        event.setCancelled();
        Messages.FOREIGN_RG.sendPopup(player);
    }
}
