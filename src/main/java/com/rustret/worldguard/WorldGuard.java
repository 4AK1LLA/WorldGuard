package com.rustret.worldguard;

import cn.nukkit.plugin.PluginBase;
import com.rustret.worldguard.commands.*;
import com.rustret.worldguard.listeners.*;

public class WorldGuard extends PluginBase {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        WorldGuardContext context = new WorldGuardContext(getConfig());
        getLogger().info("Number of loaded regions - " + context.getRegionsCount());

        getServer().getCommandMap().register("wand", new WandCommand());
        getServer().getCommandMap().register("rg", new RgCommand(context));

        getServer().getPluginManager().registerEvents(new BlockClickListener(context), this);
        getServer().getPluginManager().registerEvents(new PlayerLeaveListener(context), this);
        getServer().getPluginManager().registerEvents(new RegionProtectionListener(context), this);

        getLogger().info("Plugin Enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin Disabled");
    }
}
