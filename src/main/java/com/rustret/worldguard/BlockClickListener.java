package com.rustret.worldguard;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;

public class BlockClickListener implements Listener {

    @EventHandler
    public void onBlockClick(PlayerInteractEvent event) {

        if (event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {

            Player player = event.getPlayer();
            boolean isHoldingWand;

            try {
                isHoldingWand = player.getInventory().getItemInHand().getCustomBlockData().getBoolean("wand");
            }
            catch (NullPointerException e) {
                return;
            }

            if (isHoldingWand) {
                Block block = event.getBlock();

                int x = (int)block.getX();
                int y = (int)block.getY();
                int z = (int)block.getZ();
                String coords = x + " " + y + " " + z;

                Messages.FIRST_POS.send(player, coords);
            }
        }
    }
}
