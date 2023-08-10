package com.rustret.worldguard.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import com.rustret.worldguard.Messages;
import com.rustret.worldguard.PluginConfig;
import com.rustret.worldguard.WorldGuardContext;

import java.util.Arrays;

public class FlagCommand extends Command {
    private final WorldGuardContext context;
    private final String[] flags = { "pvp" };

    public FlagCommand(WorldGuardContext context) {
        super("flag", PluginConfig.FLAG_DESCRIPTION, "/flag");
        this.context = context;
        setPermission("worldguard.flag");

        commandParameters.clear();
        commandParameters.put("flag", new CommandParameter[] {
                CommandParameter.newType("region", CommandParamType.STRING),
                CommandParameter.newEnum("flag", new CommandEnum("flags", flags)),
                CommandParameter.newEnum("value", new CommandEnum("values", "allow", "deny")),
        });
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }

        if (args.length != 3) {
            Messages.WRONG_SYNTAX.send(sender, PluginConfig.FLAG_USAGE);
            return true;
        }

        String regionName = args[0];
        String flag = args[1];
        String value = args[2];

        if (!context.regionExists(regionName)) {
            Messages.RG_NOT_EXIST.send(sender, regionName);
            return true;
        }

        if (!Arrays.asList(flags).contains(flag)) {
            Messages.FLAG_INVALID.send(sender, args[1]);
            return true;
        }

        if (value.equals("allow")) {
            context.updateFlag(regionName, flag, true);
            Messages.FLAG_SUCCESS.send(sender, flag, regionName, "allow");
            return true;
        }

        if (value.equals("deny")) {
            context.updateFlag(regionName, flag, false);
            Messages.FLAG_SUCCESS.send(sender, flag, regionName, "deny");
            return true;
        }

        return false;
    }
}
