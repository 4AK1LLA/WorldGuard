package com.rustret.worldguard.listeners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.Event;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.ItemFrameDropItemEvent;
import cn.nukkit.event.player.PlayerBucketEmptyEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import com.rustret.worldguard.Messages;
import com.rustret.worldguard.WorldGuardContext;

import java.util.UUID;

public class RegionProtectionListener implements Listener {
    private final WorldGuardContext context;

    public RegionProtectionListener(WorldGuardContext context) {
        this.context = context;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        checkProtection(event.getBlock(), event.getPlayer(), event);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        checkProtection(event.getBlock(), event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        checkProtection(event.getBlock(), event.getPlayer(), event);
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        checkProtection(event.getBlockClicked(), event.getPlayer(), event);
    }

    @EventHandler
    public void onItemFrameDropItem(ItemFrameDropItemEvent event) {
        checkProtection(event.getBlock(), event.getPlayer(), event);
    }

    private void checkProtection(Block block, Player player, Event event) {
        String regionName = context.findRtreeRegionName(block);
        if (regionName == null) return;

        UUID id = context.getRegionOwnerId(regionName);
        if (id.equals(player.getUniqueId()) || player.hasPermission("worldguard.god")) return;

        event.setCancelled();
        Messages.FOREIGN_RG.sendPopup(player);
    }
}
