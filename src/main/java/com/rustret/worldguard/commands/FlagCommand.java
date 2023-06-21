package com.rustret.worldguard.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import com.rustret.worldguard.Messages;
import com.rustret.worldguard.WorldGuardContext;
import com.rustret.worldguard.entities.Flag;
import com.rustret.worldguard.entities.Region;

import java.util.HashMap;
import java.util.Map;

public class FlagCommand extends Command {
    private final WorldGuardContext context;
    private final Map<String, Flag> flags = new HashMap<>();

    public FlagCommand(WorldGuardContext context) {
        super("flag", "Установить значение определенного флага для региона", "/flag");
        this.context = context;
        setPermission("worldguard.flag");

        flags.put("pvp", Flag.PVP);

        commandParameters.clear();
        commandParameters.put("flag", new CommandParameter[] {
                CommandParameter.newType("region", CommandParamType.STRING),
                CommandParameter.newEnum("flag", new CommandEnum("flags", flags.keySet().toArray(new String[0]))),
                CommandParameter.newEnum("value", new CommandEnum("values", "allow", "deny")),
        });
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }

        if (args.length != 3) {
            Messages.WRONG_SYNTAX.send(sender, "/flag [регион] [флаг] [allow/deny]");
            return true;
        }

        String regionName = args[0];
        Region region = context.getRegion(regionName);
        if (region == null) {
            Messages.RG_NOT_EXIST.send(sender, regionName);
            return true;
        }

        Flag flag = flags.get(args[1]);
        if (flag == null) {
            Messages.FLAG_INVALID.send(sender, args[1]);
            return true;
        }

        switch (args[2]) {
            case "allow":
                if (!region.flags.contains(flag)) {
                    region.flags.add(flag);
                }
                Messages.FLAG_SUCCESS.send(sender, args[1], regionName, "allow");
                return true;
            case "deny":
                region.flags.remove(flag);
                Messages.FLAG_SUCCESS.send(sender, args[1], regionName, "deny");
                return true;
            default:
                Messages.WRONG_SYNTAX.send(sender, "/flag [регион] [флаг] [allow/deny]");
                return true;
        }
    }
}
