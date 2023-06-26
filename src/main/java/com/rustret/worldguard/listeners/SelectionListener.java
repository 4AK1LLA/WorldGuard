package com.rustret.worldguard.listeners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import com.rustret.worldguard.Messages;
import com.rustret.worldguard.WorldGuardContext;

public class SelectionListener implements Listener {
    private final WorldGuardContext context;

    public SelectionListener(WorldGuardContext context) {
        this.context = context;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onWandSelect(PlayerInteractEvent event) {
        if (event.isCancelled() || !event.getAction().equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        Item item = event.getItem();

        if (item.getId() != Item.WOODEN_AXE) {
            return;
        }

        if (!item.hasCustomBlockData() || !item.getCustomBlockData().getBoolean("wand")) {
            return;
        }

        Block block = event.getBlock();
        Vector3 point = new Vector3(block.getX(), block.getY(), block.getZ());

        Player player = event.getPlayer();
        long playerId = player.getId();
        Vector3[] selection = context.getSelection(playerId);

        //Init OR Reset selection and pos1
        if (selection == null || (selection[0] != null && selection[1] != null)) {
            context.setSelection(playerId, new Vector3[] { point, null });
            Messages.FIRST_POS.send(player, (int) point.x, (int) point.y, (int) point.z);
        }
        //Init pos2
        else if (selection[1] == null) {
            context.setSelection(playerId, new Vector3[] { selection[0], point });
            Messages.SECOND_POS.send(player, (int) point.x, (int) point.y, (int) point.z);
        }
    }
}
