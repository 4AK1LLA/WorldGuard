package com.rustret.worldguard.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParameter;
import com.rustret.worldguard.WorldGuardContext;

public class RgAdminCommand extends Command {
    private final WorldGuardContext context;

    public RgAdminCommand(WorldGuardContext context) {
        super("rga", "Управление регионами", "/rga");
        setPermission("worldguard.god");
        this.context = context;

        commandParameters.clear();
        commandParameters.put("flag", new CommandParameter[] {
                CommandParameter.newEnum("flag", new CommandEnum("flag", "flag")),
                CommandParameter.newEnum("flag", new CommandEnum("flags", "pvp", "mobs")),
                CommandParameter.newEnum("value", new CommandEnum("values", "allow", "deny")),
        });
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        return true;
    }
}
