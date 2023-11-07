package com.rustret.rg;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import com.rustret.rg.commands.*;
import com.rustret.rg.listeners.*;
import com.rustret.rg.selection.SelectionListener;
import com.rustret.rg.selection.SelectionManager;

public class WorldGuard extends PluginBase {
    private Messages messages;
    private WandCommand wand;
    //private RegionManager regionManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml");
        messages = new Messages(new Config(getDataFolder() + "/messages.yml"));
        wand = new WandCommand(messages);

        SelectionManager sManager = new SelectionManager();
        Server.getInstance().getPluginManager().registerEvents(new SelectionListener(sManager, messages), this);
        //getServer().getPluginManager().registerEvents(new PlayerLeaveListener(context), this);
        //getServer().getPluginManager().registerEvents(new RegionProtectionListener(context), this);

        //getLogger().info("Number of loaded regions - " + context.getRegionsCount());
        Server.getInstance().getLogger().info("[WorldGuard] Plugin enabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        switch (command.getName()) {
            case "wand": wand.execute(player); break;
            //case "rg": rg.execute();
        }
        return true;
    }

    @Override
    public void onDisable() {
        //context.saveDatabase();

        Server.getInstance().getLogger().info("[WorldGuard] Plugin disabled");
    }
}
