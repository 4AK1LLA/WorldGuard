package com.rustret.worldguard;

import cn.nukkit.plugin.PluginBase;
import com.rustret.worldguard.commands.*;
import com.rustret.worldguard.listeners.*;

public class WorldGuard extends PluginBase {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        WorldGuardContext context = new WorldGuardContext(getConfig());

//        try {
//            ConnectionSource connectionSource = new JdbcConnectionSource(connectionString);
//            TableUtils.createTableIfNotExists(connectionSource, RegionModel.class);
//
//            Dao<RegionModel, Integer> dao = DaoManager.createDao(connectionSource, RegionModel.class);
//
//            boolean isEmpty = dao.queryForAll().isEmpty();
//
//            if (isEmpty) {
//                RegionModel rg = new RegionModel("testrg", "p4ssydestr0yer", new Coord(100, 100, 60), new Coord(120, 120, 80));
//                dao.create(rg);
//            }
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//        }

        getServer().getCommandMap().register("wand", new WandCommand());
        getServer().getCommandMap().register("rg", new RgCommand(context));

        getServer().getPluginManager().registerEvents(new BlockClickListener(context), this);
        getServer().getPluginManager().registerEvents(new PlayerLeaveListener(context), this);

        getLogger().info("Plugin Enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin Disabled");
    }
}
