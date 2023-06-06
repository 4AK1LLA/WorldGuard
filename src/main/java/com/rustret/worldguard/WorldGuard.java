package com.rustret.worldguard;

import cn.nukkit.plugin.PluginBase;
import com.rustret.worldguard.commands.*;
import com.rustret.worldguard.listeners.*;

public class WorldGuard extends PluginBase {
    @Override
    public void onEnable() {
        getServer().getCommandMap().register("wand", new WandCommand());
        getServer().getCommandMap().register("rg", new RgCommand());

        getServer().getPluginManager().registerEvents(new BlockClickListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerTeleportListener(), this);

        getLogger().info("Plugin Enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin Disabled");
    }
}
