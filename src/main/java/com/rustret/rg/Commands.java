package com.rustret.rg;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.utils.Config;
import com.rustret.rg.objects.Region;
import com.rustret.rg.objects.Selection;

import java.util.*;
import java.util.stream.Collectors;

public class Commands {
    private final Storage storage;
    private final Messages messages;
    private final Item wand;
    private final int minName, maxName, maxLength, maxSize, maxMembers;
    private final String god = "rg.god";
    private final List<Integer> levels;

    public Commands(Storage storage, Messages messages, Config cfg) {
        this.storage = storage;
        this.messages = messages;

        this.minName = cfg.getInt("min-name-length");
        this.maxName = cfg.getInt("max-name-length");
        this.maxLength = cfg.getInt("max-side-length");
        this.maxSize = cfg.getInt("max-size");
        this.maxMembers = cfg.getInt("max-members");

        this.levels = cfg.getStringList("worlds")
                .stream()
                .map(name -> {
                    Level level = Server.getInstance().getLevelByName(name);
                    return level != null ? level.getId() : -1;
                })
                .filter(id -> id != -1)
                .collect(Collectors.toList());

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

        if (!name.matches("^[a-z0-9]+$")) {
            player.sendMessage(messages.CLAIM_NAME_REGEX);
            return;
        }

        UUID id = player.getUniqueId();
        Selection s = storage.selections.get(id);

        if (s == null || s.pos1 == null || s.pos2 == null) {
            player.sendMessage(messages.CLAIM_SELECTION_MISSING);
            return;
        }

        int level1 = s.pos1.level.getId();
        int level2 = s.pos2.level.getId();
        if (level1 != level2) {
            storage.removeSelection(id);
            player.sendMessage(messages.CLAIM_LEVEL_MISMATCH);
            return;
        }

        if (!player.hasPermission(god) && !levels.contains(level1)) {
            storage.removeSelection(id);
            player.sendMessage(messages.CLAIM_LEVEL_WRONG);
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

        int limit = getLimit(player);
        if (!player.hasPermission(god) && storage.getNumberOfRegions(id) >= limit) {
            player.sendMessage(String.format(messages.CLAIM_COUNT_LIMIT, limit));
            return;
        }

        if (!storage.checkIntersectAndAddRegion(name, id, s)) {
            storage.removeSelection(id);
            player.sendMessage(messages.CLAIM_INTERSECT);
            return;
        }

        storage.removeSelection(id);
        player.sendMessage(String.format(messages.CLAIM_SUCCESS, name));
        Server.getInstance()
                .getLogger()
                .info(String.format(
                        "[WorldGuard] Region created: %s | Owner: %s(%s) | Level: %s | Min: X=%d,Y=%d,Z=%d | Max: X=%d,Y=%d,Z=%d",
                        name, player.getName(), id, s.pos1.level.getName(),
                        (int) s.pos1.x, (int) s.pos1.y, (int) s.pos1.z, (int) s.pos2.x, (int) s.pos2.y, (int) s.pos2.z));
    }

    private int getLimit(Player player) {
        Optional<Integer> limit = player.getEffectivePermissions().values()
                .stream()
                .filter(info -> info.getValue() && info.getPermission().matches("rg\\.limit\\.\\d+"))
                .map(info -> Integer.parseInt(info.getPermission().replaceAll("\\D", "")))
                .max(Integer::compareTo);

        //throw new IllegalStateException("Permission with limit for regions not found");
        return limit.orElse(0);

    }

    public void delete(Player player, String name) {
        Region region = storage.regions.get(name);

        if (region == null) {
            player.sendMessage(messages.REGION_NOT_EXIST);
            return;
        }

        if (!player.hasPermission(god) && !region.owner.equals(player.getUniqueId())) {
            player.sendMessage(messages.NOT_OWNER);
            return;
        }

        storage.removeRegion(region);
        player.sendMessage(String.format(messages.DELETE_SUCCESS, name));
        Server.getInstance()
                .getLogger()
                .info(String.format("[WorldGuard] Region deleted: %s | Owner: %s(%s)",
                        name, player.getName(), region.owner));
    }

    public void addmember(Player player, String name, String memberName) {
        Region region = storage.regions.get(name);

        if (region == null) {
            player.sendMessage(messages.REGION_NOT_EXIST);
            return;
        }

        if (!player.hasPermission(god) && !region.owner.equals(player.getUniqueId())) {
            player.sendMessage(messages.NOT_OWNER);
            return;
        }

        if (region.members.size() >= maxMembers) {
            player.sendMessage(messages.MEMBER_COUNT);
            return;
        }

        Player member = Server.getInstance().getPlayerExact(memberName);
        if (member == null) {
            player.sendMessage(messages.PLAYER_NOT_FOUND);
            return;
        }

        UUID memberId = member.getUniqueId();
        if (region.owner.equals(memberId) || region.members.contains(memberId)) {
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

        if (!player.hasPermission(god) && !region.owner.equals(player.getUniqueId())) {
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
            case "mobs":
                region.mobs = val;
                player.sendMessage(String.format(messages.FLAG_SUCCESS, "mobs", value, name));
                break;
            default: player.sendMessage(messages.FLAG_WRONG); break;
        }
    }

    public void help(Player player) {
        player.sendMessage(messages.HELP);
    }

    public void info(Player player) {
        Region rg = storage.intersectedRegion(player.floor());

        if (rg == null) {
            player.sendMessage(messages.INFO_NOT_FOUND);
            return;
        }

        List<String> flags = new ArrayList<>();
        if (rg.pvp) flags.add("pvp");
        if (rg.secret) flags.add("secret");
        if (rg.mobs) flags.add("mobs");
        List<String> members = rg.members
                .stream()
                .map(id -> Server.getInstance().getOfflinePlayer(id).getName())
                .collect(Collectors.toList());

        player.sendMessage(String.format(
                messages.INFO_SUCCESS, rg.name,
                Server.getInstance().getOfflinePlayer(rg.owner).getName(), rg.owner.toString(),
                flags.isEmpty() ? messages.INFO_NONE : String.join(", ", flags), rg.level,
                (int) rg.min.x, (int) rg.min.y, (int) rg.min.z, (int) rg.max.x, (int) rg.max.y, (int) rg.max.z,
                members.isEmpty() ? messages.INFO_NONE : String.join(messages.INFO_DELIMITER, members))
        );
    }
}
