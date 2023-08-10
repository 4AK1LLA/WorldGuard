package com.rustret.worldguard.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
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

        Position position = event.getBlock();
        Player player = event.getPlayer();
        long playerId = player.getId();

        Position[] selection = context.getSelection(playerId);
        String coordinates = Messages.COORDINATES.get((int) position.x, (int) position.y, (int) position.z);

        //Init OR Reset selection and pos1
        if (selection == null || (selection[0] != null && selection[1] != null)) {
            context.setSelection(playerId, new Position[] { position, null });
            Messages.FIRST_POS.send(player, coordinates);
        }
        //Init pos2
        else if (selection[1] == null) {
            context.setSelection(playerId, new Position[] { selection[0], position });
            Messages.SECOND_POS.send(player, coordinates);
        }
    }
}
