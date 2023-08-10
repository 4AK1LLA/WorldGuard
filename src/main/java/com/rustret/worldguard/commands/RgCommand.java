package com.rustret.worldguard.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import com.rustret.worldguard.Messages;
import com.rustret.worldguard.PluginConfig;
import com.rustret.worldguard.WorldGuardContext;
import com.rustret.worldguard.entities.Region;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RgCommand extends Command {
    private final WorldGuardContext context;

    public RgCommand(WorldGuardContext context) {
        super("rg", PluginConfig.RG_DESCRIPTION, "/rg");
        commandParameters.clear();

        this.context = context;

        commandParameters.put("helpCommand", new CommandParameter[] {
                CommandParameter.newEnum("helpParameter", new CommandEnum("helpOption", "help"))
        });

        commandParameters.put("listCommand", new CommandParameter[] {
                CommandParameter.newEnum("listParameter", new CommandEnum("listOption", "list"))
        });

        commandParameters.put("claimCommand", new CommandParameter[] {
                CommandParameter.newEnum("claimParameter", new CommandEnum("claimOption", "claim")),
                CommandParameter.newType("regionName", CommandParamType.STRING)
        });

        commandParameters.put("deleteCommand", new CommandParameter[] {
                CommandParameter.newEnum("deleteParameter", new CommandEnum("deleteOption", "delete")),
                CommandParameter.newType("regionName", CommandParamType.STRING)
        });

        commandParameters.put("infoCommand", new CommandParameter[] {
                CommandParameter.newEnum("infoParameter", new CommandEnum("infoOption", "info")),
                CommandParameter.newType("regionName", CommandParamType.STRING)
        });

        commandParameters.put("addmemberCommand", new CommandParameter[] {
                CommandParameter.newEnum("addmemberParameter", new CommandEnum("addmemberOption", "addmember")),
                CommandParameter.newType("regionName", CommandParamType.STRING),
                CommandParameter.newType("player", CommandParamType.TARGET)
        });

        commandParameters.put("removememberCommand", new CommandParameter[] {
                CommandParameter.newEnum("removememberParameter", new CommandEnum("removememberOption", "removemember")),
                CommandParameter.newType("regionName", CommandParamType.STRING),
                CommandParameter.newType("player", CommandParamType.TARGET)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!sender.isPlayer()) {
            Messages.NOT_PLAYER.send(sender);
            return true;
        }

        if (args.length == 0) {
            Messages.RG.send(sender);
            return true;
        }

        switch (args[0]) {
            case "help":
                return help(sender, args);
            case "claim":
                return claim(sender, args);
            case "delete":
                return delete(sender, args);
            case "addmember":
                return addmember(sender, args);
            case "removemember":
                return removemember(sender, args);
            case "list":
                return list(sender, args);
            case "info":
                return info(sender, args);
            default:
                Messages.RG_WRONG.send(sender);
                return true;
        }
    }

    private boolean claim(CommandSender sender, String[] args) {
        if (args.length != 2) {
            Messages.WRONG_SYNTAX.send(sender, PluginConfig.RG_CLAIM_USAGE);
            return true;
        }

        String regionName = args[1];

        int length = regionName.length();
        if (length < 3 || length > 12) {
            Messages.RG_NAME_LENGTH.send(sender);
            return true;
        }

        //Latin letters and numbers check
        String pattern = "^[a-z0-9]+$";
        if (!Pattern.matches(pattern, regionName)) {
            Messages.RG_NAME_REGEX.send(sender);
            return true;
        }

        Player player = (Player)sender;
        Position[] selection = context.getSelection(player.getId());

        if (selection == null || selection[0] == null || selection[1] == null) {
            Messages.MISSIING_SELECTION.send(sender);
            return true;
        }

        int firstLevelId = selection[0].getLevel().getId();
        if (firstLevelId != selection[1].getLevel().getId()) {
            Messages.LEVEL_DIFFERENCE.send(sender);
            return true;
        }

        int xLength = (int) (Math.abs(selection[1].x - selection[0].x) + 1);
        int yLength = (int) (Math.abs(selection[1].y - selection[0].y) + 1);
        int zLength = (int) (Math.abs(selection[1].z - selection[0].z) + 1);

        //TODO: Add all limits to config
        if (!player.hasPermission("worldguard.god") && (xLength > 50 || yLength > 50 || zLength > 50)) {
            context.removeSelection(player.getId());
            Messages.RG_SIDE_LIMIT.send(sender);
            return true;
        }

        int regionSize = xLength * yLength * zLength;
        if (!player.hasPermission("worldguard.god") && regionSize > 50000) {
            context.removeSelection(player.getId());
            Messages.RG_SIZE_LIMIT.send(sender);
            return true;
        }

        if (context.regionExists(regionName)) {
            Messages.RG_EXIST.send(sender, regionName);
            return true;
        }

        UUID ownerId = player.getUniqueId();
        if (!player.hasPermission("worldguard.god") && context.getPlayerRegionsCount(ownerId) >= 2) {
            context.removeSelection(player.getId());
            Messages.RG_COUNT_LIMIT.send(sender);
            return true;
        }

        if (context.intersectsRegion(selection, firstLevelId)) {
            context.removeSelection(player.getId());
            Messages.RG_INTERSECT.send(sender);
            return true;
        }

        context.addRegion(regionName, player.getName(), ownerId, selection, firstLevelId);
        context.removeSelection(player.getId());

        Messages.RG_CLAIM.send(sender, regionName, regionSize);
        return true;
    }

    private boolean delete(CommandSender sender, String[] args) {
        if (args.length != 2) {
            Messages.WRONG_SYNTAX.send(sender, PluginConfig.RG_DELETE_USAGE);
            return true;
        }

        String regionName = args[1];

        int length = regionName.length();
        if (length < 3 || length > 12) {
            Messages.RG_NAME_LENGTH.send(sender);
            return true;
        }

        //Latin letters and numbers check
        String pattern = "^[a-z0-9]+$";
        if (!Pattern.matches(pattern, regionName)) {
            Messages.RG_NAME_REGEX.send(sender);
            return true;
        }

        if (!context.regionExists(regionName)) {
            Messages.RG_NOT_EXIST.send(sender, regionName);
            return true;
        }

        Player player = (Player)sender;

        boolean ownerIsValid = context.getRegionOwnerId(regionName).equals(player.getUniqueId());
        if (!ownerIsValid && !player.hasPermission("worldguard.god")) {
            Messages.RG_NOT_OWNER.send(sender);
            return true;
        }

        context.removeRegion(regionName);
        Messages.RG_DELETE.send(sender, regionName);
        return true;
    }

    private boolean help(CommandSender sender, String[] args) {
        if (args.length != 1) {
            Messages.WRONG_SYNTAX.send(sender, "/rg help");
            return true;
        }

        Messages.RG_HELP.send(sender);
        return true;
    }

    private boolean addmember(CommandSender sender, String[] args) {
        if (args.length != 3) {
            Messages.WRONG_SYNTAX.send(sender, PluginConfig.RG_ADDMEMBER_USAGE);
            return true;
        }

        String regionName = args[1];

        int length = regionName.length();
        if (length < 3 || length > 12) {
            Messages.RG_NAME_LENGTH.send(sender);
            return true;
        }

        //Latin letters and numbers check
        String pattern = "^[a-z0-9]+$";
        if (!Pattern.matches(pattern, regionName)) {
            Messages.RG_NAME_REGEX.send(sender);
            return true;
        }

        if (!context.regionExists(regionName)) {
            Messages.RG_NOT_EXIST.send(sender, regionName);
            return true;
        }

        Player player = (Player)sender;
        UUID ownerId = context.getRegionOwnerId(regionName);
        if (!ownerId.equals(player.getUniqueId())) {
            Messages.RG_NOT_OWNER.send(sender);
            return true;
        }

        if (context.getRegionMembersCount(regionName) > 5) {
            Messages.MEMBERS_LIMIT.send(sender);
            return true;
        }

        String memberName = args[2];
        UUID memberId = context.findPlayerId(memberName);
        if (memberId == null) {
            Messages.PLAYER_OFFLINE.send(sender);
            return true;
        }

        if (ownerId.equals(memberId) || context.memberExists(regionName, memberId)) {
            Messages.MEMBER_EXIST.send(sender);
            return true;
        }

        context.addMember(regionName, memberId);
        Messages.ADDMEMBER_SUCCESS.send(sender, memberName, regionName);
        return true;
    }

    private boolean removemember(CommandSender sender, String[] args) {
        if (args.length != 3) {
            Messages.WRONG_SYNTAX.send(sender, PluginConfig.RG_REMOVEMEMBER_USAGE);
            return true;
        }

        String regionName = args[1];

        int length = regionName.length();
        if (length < 3 || length > 12) {
            Messages.RG_NAME_LENGTH.send(sender);
            return true;
        }

        //Latin letters and numbers check
        String pattern = "^[a-z0-9]+$";
        if (!Pattern.matches(pattern, regionName)) {
            Messages.RG_NAME_REGEX.send(sender);
            return true;
        }

        if (!context.regionExists(regionName)) {
            Messages.RG_NOT_EXIST.send(sender, regionName);
            return true;
        }

        Player player = (Player)sender;

        boolean ownerIsValid = context.getRegionOwnerId(regionName).equals(player.getUniqueId());
        if (!ownerIsValid) {
            Messages.RG_NOT_OWNER.send(sender);
            return true;
        }

        String memberName = args[2];
        if (!context.removeMember(regionName, memberName)) {
            Messages.MEMBER_NOT_EXIST.send(sender, memberName, regionName);
            return true;
        }

        Messages.REMOVEMEMBER_SUCCESS.send(sender, memberName, regionName);
        return true;
    }

    private boolean list(CommandSender sender, String[] args) {
        if (args.length != 1) {
            Messages.WRONG_SYNTAX.send(sender, "/rg list");
            return true;
        }

        Player player = (Player) sender;
        List<String> regionNames = context.getPlayerRegionNames(player.getUniqueId());

        if (regionNames.isEmpty()) {
            Messages.REGIONS_EMPTY.send(player);
            return true;
        }

        Messages.RG_LIST.send(player, String.join(", ", regionNames));
        return true;
    }

    private boolean info(CommandSender sender, String[] args) {
        if (args.length != 2) {
            Messages.WRONG_SYNTAX.send(sender, "/rg info");
            return true;
        }

        String regionName = args[1];

        if (!context.regionExists(regionName)) {
            Messages.RG_NOT_EXIST.send(sender, regionName);
            return true;
        }

        Region region = context.getRegion(regionName);

        String ownerName = Server.getInstance().getOfflinePlayer(region.ownerId).getName();

        List<String> memberNamesList = region.memberIds.stream()
                .map(memberId -> Server.getInstance().getOfflinePlayer(memberId).getName())
                .collect(Collectors.toList());

        String memberNames = (memberNamesList.isEmpty()) ? Messages.RG_NO_MEMBERS.get() : String.join(", ", memberNamesList);

        String point1 = Messages.COORDINATES.get((int) region.coordinates[0].x, (int) region.coordinates[0].y, (int) region.coordinates[0].z);
        String point2 = Messages.COORDINATES.get((int) region.coordinates[1].x, (int) region.coordinates[1].y, (int) region.coordinates[1].z);

        Messages.RG_INFO.send(sender, regionName, ownerName, memberNames, point1, point2);
        return true;
    }
}
