package com.rustret.worldguard;

import cn.nukkit.plugin.PluginBase;
import com.rustret.worldguard.commands.*;
import com.rustret.worldguard.listeners.*;

public class WorldGuard extends PluginBase {
    private WorldGuardContext context;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        context = new WorldGuardContext(getConfig());
        getLogger().info("Number of loaded regions - " + context.getRegionsCount());

        getServer().getCommandMap().register("wand", new WandCommand());
        getServer().getCommandMap().register("rg", new RgCommand(context));
        getServer().getCommandMap().register("flag", new FlagCommand(context));

        getServer().getPluginManager().registerEvents(new SelectionListener(context), this);
        getServer().getPluginManager().registerEvents(new PlayerLeaveListener(context), this);
        getServer().getPluginManager().registerEvents(new RegionProtectionListener(context), this);

        getLogger().info("Plugin Enabled");
    }

    @Override
    public void onDisable() {
        context.saveDatabase();

        getLogger().info("Plugin Disabled");
    }
}
