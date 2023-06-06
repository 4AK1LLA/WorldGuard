package com.rustret.worldguard;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import com.rustret.worldguard.commands.WandCommand;
import com.rustret.worldguard.listeners.BlockClickListener;
import com.rustret.worldguard.listeners.PlayerQuitListener;
import com.rustret.worldguard.listeners.PlayerTeleportListener;

public class WorldGuard extends PluginBase {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new BlockClickListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerTeleportListener(), this);

        getLogger().info("Plugin Enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin Disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("wand")) {
            WandCommand.handle(sender, args);

            return true;
        }

        if (command.getName().equalsIgnoreCase("rg")) {
            //RgCommand.handle();
        }

        return false;
    }
}
