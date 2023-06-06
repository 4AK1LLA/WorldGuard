package com.rustret.worldguard.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.nbt.tag.CompoundTag;
import com.rustret.worldguard.Messages;

import java.util.UUID;

public class WandCommand {

    public static boolean handle(CommandSender sender, String[] args) {
        if (!sender.isPlayer()) {
            Messages.NOT_PLAYER.send(sender);
            return true;
        }

        if (args.length > 0) {
            Messages.WRONG_SYNTAX.send(sender, "/wand");
            return true;
        }

        Player player = (Player)sender;

        if (player.getInventory().isFull()) {
            Messages.FULL_INVENTORY.send(sender);
            return true;
        }

        Item wandAxe = new Item(ItemID.WOODEN_AXE);

        if (!player.getInventory().canAddItem(wandAxe)) {
            Messages.FULL_INVENTORY.send(sender);
            return true;
        }

        CompoundTag wandTag = new CompoundTag();
        wandTag.putString("uuid", UUID.randomUUID().toString());
        wandTag.putBoolean("wand", true);
        wandAxe.setCustomBlockData(wandTag);
        wandAxe.setCustomName("Wand топор");

        player.getInventory().addItem(wandAxe);
        Messages.WAND_SUCCESS.send(sender);

        return true;
    }
}
