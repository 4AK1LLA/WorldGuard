package com.rustret.rg;

import cn.nukkit.Player;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.Config;
import com.rustret.rg.objects.Selection;

import java.util.UUID;

public class Commands {
    private final Storage storage;
    private final Messages messages;
    private final Item wand;
    private final int minName, maxName, maxLength, maxSize;
    private final String regex = "^[a-z0-9]+$";

    public Commands(Storage storage, Messages messages, Config cfg) {
        this.storage = storage;
        this.messages = messages;

        this.minName = cfg.getInt("min-name-length");
        this.maxName = cfg.getInt("max-name-length");
        this.maxLength = cfg.getInt("max-side-length");
        this.maxSize = cfg.getInt("max-size");

        String wandName = cfg.getString("wand-name");
        this.wand = Item
                .get(Item.WOODEN_AXE)
                .setCustomName(wandName);
        wand.addEnchantment(Enchantment.get(-1));
    }

    public void claim(Player player, String name) {
        int nameLength = name.length();
        if (nameLength < minName || nameLength > maxName) {
            player.sendMessage(messages.CLAIM_NAME_LENGTH);
            return;
        }

        if (!name.matches(regex)) {
            player.sendMessage(messages.CLAIM_NAME_REGEX);
            return;
        }

        UUID id = player.getUniqueId();
        Selection s = storage.selections.get(id);

        if (s == null || s.pos1 == null || s.pos2 == null) {
            player.sendMessage(messages.CLAIM_SELECTION_MISSING);
            return;
        }

        if (s.pos1.level.getId() != s.pos2.level.getId()) {
            storage.removeSelection(id);
            player.sendMessage(messages.CLAIM_LEVEL_MISMATCH);
            return;
        }

        s.align();
        int xLength = (int) (s.pos2.x - s.pos1.x + 1);
        int yLength = (int) (s.pos2.y - s.pos1.y + 1);
        int zLength = (int) (s.pos2.z - s.pos1.z + 1);
        if (!player.hasPermission("rg.god") &&
                (xLength > maxLength || yLength > maxLength || zLength > maxLength)) {
            storage.removeSelection(id);
            player.sendMessage(messages.CLAIM_SIDE_LENGTH);
            return;
        }

        int size = xLength * yLength * zLength;
        if (!player.hasPermission("rg.god") && size > maxSize) {
            storage.removeSelection(id);
            player.sendMessage(messages.CLAIM_SIZE);
            return;
        }

        if (storage.regions.containsKey(name)) {
            player.sendMessage(messages.CLAIM_REGION_EXIST);
            return;
        }

        if (!player.hasPermission("rg.god") && storage.getNumberOfRegions(id) > 2) {
            player.sendMessage(messages.CLAIM_COUNT_LIMIT);
            return;
        }

        if (!storage.checkIntersectAndAdd(name, id, s)) {
            storage.removeSelection(id);
            player.sendMessage(messages.CLAIM_INTERSECT);
            return;
        }

        storage.removeSelection(id);
        player.sendMessage(messages.CLAIM_SUCCESS);
    } // W
    public void delete(Player player, String name) {}
    public void addmember(Player player, String name, String member) {}
    public void removemember(Player player, String name, String member) {}
    public void list(Player player) {}
    public void wand(Player player) {
        PlayerInventory inventory = player.getInventory();

        if (!inventory.canAddItem(wand)) {
            player.sendMessage(messages.FULL_INVENTORY);
            return;
        }

        inventory.addItem(wand);
        player.sendMessage(messages.WAND_SUCCESS);
    } // W
    public void flag(Player player, String name, String flag, String value) {}
    public void help(Player player) {
        player.sendMessage(messages.HELP);
    } // W
}
