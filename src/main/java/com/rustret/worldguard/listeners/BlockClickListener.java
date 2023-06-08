package com.rustret.worldguard.listeners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import com.rustret.worldguard.coordinates.Coord;
import com.rustret.worldguard.WorldGuardContext;

public class BlockClickListener implements Listener {
    private final WorldGuardContext context;

    public BlockClickListener(WorldGuardContext context) {
        this.context = context;
    }

    @EventHandler
    public void onBlockClick(PlayerInteractEvent event) {
        if (event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            boolean isHoldingWand;

            try {
                Item item = event.getItem();
                isHoldingWand = item.getCustomBlockData().getBoolean("wand");
            }
            catch (NullPointerException e) {
                return;
            }

            if (isHoldingWand) {
                Block block = event.getBlock();

                int x = (int)block.getX();
                int y = (int)block.getY();
                int z = (int)block.getZ();
                Coord coords = new Coord(x, y, z);

                context.setPlayerSelection(player, coords);
            }
        }
    }
}
