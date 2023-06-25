package com.rustret.worldguard.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.math.Vector3;
import com.rustret.worldguard.Messages;
import com.rustret.worldguard.entities.Region;
import com.rustret.worldguard.WorldGuardContext;

import java.util.UUID;
import java.util.regex.Pattern;

public class RgCommand extends Command {
    private final WorldGuardContext context;

    public RgCommand(WorldGuardContext context) {
        super("rg", "Создание и редактирование регионов", "/rg");
        commandParameters.clear();

        this.context = context;

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

//        commandParameters.put("addmemberCommand", new CommandParameter[] {
//                CommandParameter.newEnum("addmemberParameter", new CommandEnum("addmemberOption", "addmember")),
//                CommandParameter.newType("player", CommandParamType.TARGET)
//        });
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
        Vector3[] selection = context.getSelection(player);

        if (selection == null || selection[0] == null || selection[1] == null) {
            Messages.MISSIING_SELECTION.send(sender);
            return true;
        }

        int xLength = (int) (Math.abs(selection[1].x - selection[0].x) + 1);
        int yLength = (int) (Math.abs(selection[1].y - selection[0].y) + 1);
        int zLength = (int) (Math.abs(selection[1].z - selection[0].z) + 1);

        //TODO: Add all limits to config
        if (!player.hasPermission("worldguard.god") && (xLength > 50 || yLength > 50 || zLength > 50)) {
            context.removePlayerSelection(player);
            Messages.RG_SIDE_LIMIT.send(sender);
            return true;
        }

        int regionSize = xLength * yLength * zLength;
        if (!player.hasPermission("worldguard.god") && regionSize > 50000) {
            context.removePlayerSelection(player);
            Messages.RG_SIZE_LIMIT.send(sender);
            return true;
        }

        if (context.getRegion(regionName) != null) {
            Messages.RG_EXIST.send(sender, regionName);
            return true;
        }

        UUID ownerId = player.getUniqueId();
        if (!player.hasPermission("worldguard.god") && context.getPlayerRegionsCount(ownerId) >= 2) {
            context.removePlayerSelection(player);
            Messages.RG_COUNT_LIMIT.send(sender);
            return true;
        }

        if (context.intersectsRegion(selection)) {
            context.removePlayerSelection(player);
            Messages.RG_INTERSECT.send(sender);
            return true;
        }

        String ownerName = player.getName();
        Region region = new Region(regionName, ownerName, ownerId, selection);

        if (!context.addRegion(regionName, region)) {
            return false;
        }

        context.removePlayerSelection(player);
        Messages.RG_CLAIM.send(sender, regionName, regionSize);
        return true;
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

        boolean ownerIsValid = region.ownerId.equals(player.getUniqueId());
        if (!ownerIsValid && !player.hasPermission("worldguard.god")) {
            Messages.RG_NOT_OWNER.send(sender);
            return false;
        }

        if (!context.removeRegion(regionName)) {
            return false;
        }

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
}
