package com.rustret.rg.commands;

import cn.nukkit.Player;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import com.rustret.rg.Messages;

import java.util.UUID;

public class WandCommand {
    private final Messages messages;
    private final Item wand;

    public WandCommand(Messages messages) {
        this.messages = messages;
        wand = Item
                .get(Item.WOODEN_AXE)
                .setCustomName("wand axe");
        wand.addEnchantment(Enchantment.get(-1));
    }

    public void execute(Player player) {
        PlayerInventory inventory = player.getInventory();

        if (!inventory.canAddItem(wand)) {
            player.sendMessage(messages.FULL_INVENTORY);
            return;
        }

        inventory.addItem(wand);
        player.sendMessage(messages.WAND);
    }
}
