package com.rustret.worldguard.listeners;

import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import com.rustret.worldguard.WorldGuardContext;

public class BlockBreakListener implements Listener {
    private final WorldGuardContext context;

    public BlockBreakListener(WorldGuardContext context) {
        this.context = context;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (context.blockIsPrivated(block)) {
            event.getPlayer().sendMessage("Block is in region");
        }
        else {
            event.getPlayer().sendMessage("Block is not in region");
        }

        //event.setCancelled();
    }
}
