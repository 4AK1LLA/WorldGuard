package com.rustret.worldguard.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import com.rustret.worldguard.Messages;
import com.rustret.worldguard.Region;
import com.rustret.worldguard.WorldGuardContext;
import com.rustret.worldguard.coordinates.Coord;
import com.rustret.worldguard.coordinates.CoordPair;

import java.util.regex.Pattern;

public class RgCommand extends Command {
    private final WorldGuardContext context;

    public RgCommand(WorldGuardContext context) {
        super("rg", "Создание и редактирование регионов", "/rg");

        this.context = context;

        commandParameters.clear();

        commandParameters.put("helpCommand", new CommandParameter[] {
                CommandParameter.newEnum("helpParameter", new CommandEnum("helpOption", "help"))
        });

        commandParameters.put("claimCommand", new CommandParameter[] {
                CommandParameter.newEnum("claimParameter", new CommandEnum("claimOption", "claim")),
                CommandParameter.newType("regionName", CommandParamType.STRING)
        });

        commandParameters.put("deleteCommand", new CommandParameter[] {
                CommandParameter.newEnum("deleteParameter", new CommandEnum("deleteOption", "delete")),
                CommandParameter.newType("regionName", CommandParamType.STRING)
        });

        commandParameters.put("addmemberCommand", new CommandParameter[] {
                CommandParameter.newEnum("addmemberParameter", new CommandEnum("addmemberOption", "addmember")),
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
            default:
                Messages.RG_WRONG.send(sender);
                return true;
        }
    }

    private boolean claim(CommandSender sender, String[] args) {
        if (args.length != 2) {
            Messages.WRONG_SYNTAX.send(sender, "/rg claim [название региона]");
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
        CoordPair selection = context.getPlayerSelection(player);

        if (selection == null || selection.pos1 == null || selection.pos2 == null) {
            Messages.MISSIING_SELECTION.send(sender);
            return true;
        }

        int xLength = Math.abs(selection.pos2.x - selection.pos1.x) + 1;
        int yLength = Math.abs(selection.pos2.y - selection.pos1.y) + 1;
        int zLength = Math.abs(selection.pos2.z - selection.pos1.z) + 1;

        //TODO: Add all limits to config
        if (!player.hasPermission("worldguard.god") && (xLength > 50 || yLength > 50 || zLength > 50)) {
            Messages.RG_SIDE_LIMIT.send(sender);
            return true;
        }

        int regionSize = xLength * yLength * zLength;
        if (!player.hasPermission("worldguard.god") && regionSize > 50000) {
            Messages.RG_SIZE_LIMIT.send(sender);
            return true;
        }

        if (context.getRegion(regionName) != null) {
            Messages.RG_EXIST.send(sender, regionName);
            return true;
        }

        if (context.intersectsRegion(selection)) {
            context.removePlayerSelection(player);
            Messages.RG_INTERSECT.send(sender);
            return true;
        }

        String ownerName = player.getName();
        String ownerId = player.getUniqueId().toString();
        Coord pos1 = new Coord(selection.pos1.x, selection.pos1.y, selection.pos1.z);
        Coord pos2 = new Coord(selection.pos2.x, selection.pos2.y, selection.pos2.z);
        Region region = new Region(ownerName, ownerId, pos1, pos2);

        if (context.addRegion(regionName, region)) {
            context.removePlayerSelection(player);
            Messages.RG_CLAIM.send(sender, regionName, regionSize);
            return true;
        }

        return false;
    }

    private boolean delete(CommandSender sender, String[] args) {
        if (args.length != 2) {
            Messages.WRONG_SYNTAX.send(sender, "/rg delete [название региона]");
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

        Region region = context.getRegion(regionName);

        if (region == null) {
            Messages.RG_NOT_EXIST.send(sender, regionName);
            return false;
        }

        Player player = (Player)sender;

        boolean ownerIsValid = region.ownerId.equals(player.getUniqueId().toString());
        if (!ownerIsValid && !player.hasPermission("worldguard.god")) {
            Messages.RG_NOT_OWNER.send(sender);
            return false;
        }

        if (context.removeRegion(regionName)) {
            Messages.RG_DELETE.send(sender, regionName);
            return true;
        }

        return false;
    }

    private boolean help(CommandSender sender, String[] args) {
        if (args.length != 1) {
            Messages.WRONG_SYNTAX.send(sender, "/rg help");
            return true;
        }

        Messages.RG_HELP.send(sender);
        return true;
    }
}
