package com.rustret.worldguard.listeners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import com.rustret.worldguard.coordinates.Coord;
import com.rustret.worldguard.WorldGuardContext;

public class SelectionListener implements Listener {
    private final WorldGuardContext context;

    public SelectionListener(WorldGuardContext context) {
        this.context = context;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockClick(PlayerInteractEvent event) {
        if (event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            Item item = event.getItem();

            if (item.getId() != Item.WOODEN_AXE) {
                return;
            }

            if (!item.hasCustomBlockData() || !item.getCustomBlockData().getBoolean("wand")) {
                return;
            }

            Block block = event.getBlock();

            int x = (int)block.getX();
            int y = (int)block.getY();
            int z = (int)block.getZ();
            Coord coords = new Coord(x, y, z);
            Player player = event.getPlayer();

            context.setPlayerSelection(player, coords);
        }
    }
}
