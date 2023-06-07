package com.rustret.worldguard;

import cn.nukkit.plugin.PluginBase;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.rustret.worldguard.commands.*;
import com.rustret.worldguard.listeners.*;

import java.sql.SQLException;

public class WorldGuard extends PluginBase {
    @Override
    public void onEnable() {
        try {
            ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:mysql://localhost/worldguard?user=root&password=Pa$$w0rd&serverTimezone=UTC");
            TableUtils.createTableIfNotExists(connectionSource, Region.class);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

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
