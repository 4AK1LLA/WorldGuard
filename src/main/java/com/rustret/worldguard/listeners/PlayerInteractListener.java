package com.rustret.worldguard.listeners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.DoorToggleEvent;
import cn.nukkit.event.inventory.InventoryOpenEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import com.rustret.worldguard.Messages;
import com.rustret.worldguard.WorldGuardContext;
import com.rustret.worldguard.coordinates.Coord;

public class PlayerInteractListener implements Listener {
    private final WorldGuardContext context;

    public PlayerInteractListener(WorldGuardContext context) {
        this.context = context;
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();

        if (inventory.getType().equals(InventoryType.PLAYER)) {
            return;
        }

        Position position = (Position) inventory.getHolder();
        Player player = event.getPlayer();

        if (context.canInteract(position, player)) {
            return;
        }

        event.setCancelled();
        Messages.FOREIGN_RG.send(player);
    }

    //trapdoor, door, fence gate
    @EventHandler
    public void onDoorToggle(DoorToggleEvent event) {

    }
}
