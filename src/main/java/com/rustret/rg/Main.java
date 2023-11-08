package com.rustret.rg;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
//import com.rustret.rg.selection.SelectionListener;

public class Main extends PluginBase {
    private Messages messages;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml");

        Storage storage = new Storage();

        this.messages = new Messages(new Config(getDataFolder() + "/messages.yml"));

        // Adding commands and listeners
//        Commands cmd = new Commands(storage, messages);
//        Server.getInstance()
//                .getPluginManager()
//                .registerEvents(new SelectionListener(storage, messages), this);

        Server.getInstance()
                .getLogger()
                .info("[WorldGuard] Plugin enabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        switch (args[0]) {
//            case "claim": cmd.claim(player, args[1]); break;
//            case "delete": cmd.delete(player, args[1]); break;
//            case "addmember": cmd.addmember(player, args[1], args[2]); break;
//            case "removemember": cmd.removemember(player, args[1], args[2]); break;
//            case "list": cmd.list(player); break;
//            case "wand": cmd.wand(player); break;
//            case "flag": cmd.flag(player, args[1], args[2], args[3]);
//            default: cmd.help(player); break;
        }

        return true;
    }

    @Override
    public void onDisable() {
        Server.getInstance()
                .getLogger()
                .info("[WorldGuard] Plugin disabled");
    }
}
