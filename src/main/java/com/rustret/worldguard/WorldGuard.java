package com.rustret.worldguard;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.rustret.worldguard.commands.*;
import com.rustret.worldguard.coordinates.Coord;
import com.rustret.worldguard.dbmodels.RegionModel;
import com.rustret.worldguard.listeners.*;

import java.sql.SQLException;

public class WorldGuard extends PluginBase {
    @Override
    public void onEnable() {
        saveDefaultConfig();

        Config config = getConfig();
        String connectionString = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s",
                config.get("MySql.host"), (int)config.get("MySql.port"), config.get("MySql.database"),
                config.get("MySql.username"), config.get("MySql.password"));

        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(connectionString);
            TableUtils.createTableIfNotExists(connectionSource, RegionModel.class);

            Dao<RegionModel, Integer> dao = DaoManager.createDao(connectionSource, RegionModel.class);

            boolean isEmpty = dao.queryForAll().isEmpty();

            if (isEmpty) {
                RegionModel rg = new RegionModel("testrg", "p4ssydestr0yer", new Coord(100, 100, 60), new Coord(120, 120, 80));
                dao.create(rg);
            }
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
