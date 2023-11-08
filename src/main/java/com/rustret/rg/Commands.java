package com.rustret.rg;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.Config;
import com.rustret.rg.objects.Region;
import com.rustret.rg.objects.Selection;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Commands {
    private final Storage storage;
    private final Messages messages;
    private final Item wand;
    private final int minName, maxName, maxLength, maxSize, maxMembers;
    private final String regex = "^[a-z0-9]+$", god = "rg.god";

    public Commands(Storage storage, Messages messages, Config cfg) {
        this.storage = storage;
        this.messages = messages;

        this.minName = cfg.getInt("min-name-length");
        this.maxName = cfg.getInt("max-name-length");
        this.maxLength = cfg.getInt("max-side-length");
        this.maxSize = cfg.getInt("max-size");
        this.maxMembers = cfg.getInt("max-members");

        String wandName = cfg.getString("wand-name");
        this.wand = Item
                .get(Item.WOODEN_AXE)
                .setCustomName(wandName);
        wand.addEnchantment(Enchantment.get(-1));
    }

    public void claim(Player player, String name) {
        int nameLength = name.length();
        if (nameLength < minName || nameLength > maxName) {
            player.sendMessage(String.format(messages.CLAIM_NAME_LENGTH, minName, maxName));
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
        if (!player.hasPermission(god) &&
                (xLength > maxLength || yLength > maxLength || zLength > maxLength)) {
            storage.removeSelection(id);
            player.sendMessage(String.format(messages.CLAIM_SIDE_LENGTH, maxLength));
            return;
        }

        int size = xLength * yLength * zLength;
        if (!player.hasPermission(god) && size > maxSize) {
            storage.removeSelection(id);
            player.sendMessage(String.format(messages.CLAIM_SIZE, maxSize));
            return;
        }

        if (storage.regions.containsKey(name)) {
            player.sendMessage(String.format(messages.CLAIM_REGION_EXIST, name));
            return;
        }

        if (!player.hasPermission(god) && storage.getNumberOfRegions(id) >= 2) {
            player.sendMessage(messages.CLAIM_COUNT_LIMIT);
            return;
        }

        if (!storage.checkIntersectAndAddRegion(name, id, s)) {
            storage.removeSelection(id);
            player.sendMessage(messages.CLAIM_INTERSECT);
            return;
        }

        storage.removeSelection(id);
        player.sendMessage(String.format(messages.CLAIM_SUCCESS, name));
    }

    public void delete(Player player, String name) {
        Region region = storage.regions.get(name);

        if (region == null) {
            player.sendMessage(messages.REGION_NOT_EXIST);
            return;
        }

        if (!player.hasPermission(god) && region.owner != player.getUniqueId()) {
            player.sendMessage(messages.NOT_OWNER);
            return;
        }

        storage.removeRegion(region);
        player.sendMessage(String.format(messages.DELETE_SUCCESS, name));
    }

    public void addmember(Player player, String name, String memberName) {
        Region region = storage.regions.get(name);

        if (region == null) {
            player.sendMessage(messages.REGION_NOT_EXIST);
            return;
        }

        if (!player.hasPermission(god) && region.owner != player.getUniqueId()) {
            player.sendMessage(messages.NOT_OWNER);
            return;
        }

        if (region.members.size() >= maxMembers) {
            player.sendMessage(messages.MEMBER_COUNT);
            return;
        }

        Player member = Server.getInstance().getPlayer(memberName);
        if (member == null) {
            player.sendMessage(messages.PLAYER_NOT_FOUND);
            return;
        }

        UUID memberId = member.getUniqueId();
        if (region.owner == memberId || region.members.contains(memberId)) {
            player.sendMessage(messages.MEMBER_EXIST);
            return;
        }

        region.members.add(memberId);
        player.sendMessage(String.format(messages.ADDMEMBER_SUCCESS, member.getName(), name));
    }

    public void removemember(Player player, String name, String memberName) {
        Region region = storage.regions.get(name);

        if (region == null) {
            player.sendMessage(messages.REGION_NOT_EXIST);
            return;
        }

        if (!player.hasPermission(god) && region.owner != player.getUniqueId()) {
            player.sendMessage(messages.NOT_OWNER);
            return;
        }

        boolean removed = region.members.removeIf(id -> Server.getInstance()
                .getOfflinePlayer(id)
                .getName()
                .equals(memberName));
        player.sendMessage(removed
                ? String.format(messages.REMOVEMEMBER_SUCCESS, memberName, name)
                : messages.MEMBER_NOT_FOUND);
    }

    public void list(Player player) {
        List<Region> regions = storage.players.get(player.getUniqueId());

        if (regions == null || regions.isEmpty()) {
            player.sendMessage(messages.LIST_EMPTY);
            return;
        }

        StringBuilder sb = new StringBuilder(messages.LIST_START);
        for (Region region : regions) {
            List<String> names = region.members
                    .stream()
                    .map(id -> Server.getInstance().getOfflinePlayer(id).getName())
                    .collect(Collectors.toList());

            sb.append(String.format(messages.LIST_ENTRY,
                            region.name, (int) region.max.x, (int) region.max.y, (int) region.max.z))
                    .append(names.isEmpty()
                            ? messages.LIST_NO_MEMBERS
                            : String.join(messages.LIST_DELIMITER, names));
        }

        player.sendMessage(sb.toString());
    }

    public void wand(Player player) {
        PlayerInventory inventory = player.getInventory();

        if (!inventory.canAddItem(wand)) {
            player.sendMessage(messages.FULL_INVENTORY);
            return;
        }

        inventory.addItem(wand);
        player.sendMessage(messages.WAND_SUCCESS);
    }

    public void flag(Player player, String name, String flag, String value) {
        Region region = storage.regions.get(name);

        if (region == null) {
            player.sendMessage(messages.REGION_NOT_EXIST);
            return;
        }

        boolean val;
        switch (value) {
            case "allow": val = true; break;
            case "deny": val = false; break;
            default: player.sendMessage(messages.FLAG_VALUE); return;
        }

        switch (flag) {
            case "pvp":
                region.pvp = val;
                player.sendMessage(String.format(messages.FLAG_SUCCESS, "pvp", value, name));
                break;
            case "secret":
                region.secret = val;
                player.sendMessage(String.format(messages.FLAG_SUCCESS, "secret", value, name));
                break;
            default: player.sendMessage(messages.FLAG_WRONG); break;
        }
    }

    public void help(Player player) {
        player.sendMessage(messages.HELP);
    }
}
