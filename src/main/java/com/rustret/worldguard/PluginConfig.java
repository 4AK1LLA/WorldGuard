package com.rustret.worldguard;

import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.Config;

public class PluginConfig {
    private static Config messages, config;

    public static void saveConfigs(Plugin plugin) {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();

        plugin.saveResource("messages.yml");
        messages = new Config(plugin.getDataFolder() + "/messages.yml");

        FLAG_DESCRIPTION = messages.getString("command-descriptions.flag");
        RG_DESCRIPTION = messages.getString("command-descriptions.rg");
        WAND_DESCRIPTION = messages.getString("command-descriptions.wand");
        FLAG_USAGE = messages.getString("command-usages.flag");
        RG_CLAIM_USAGE = messages.getString("command-usages.rg-claim");
        RG_DELETE_USAGE = messages.getString("command-usages.rg-delete");
        RG_INFO_EXT_USAGE = messages.getString("command-usages.rg-info-ext");
        RG_ADDMEMBER_USAGE = messages.getString("command-usages.rg-addmember");
        RG_REMOVEMEMBER_USAGE = messages.getString("command-usages.rg-removemember");
    }

    public static String FLAG_DESCRIPTION, RG_DESCRIPTION, WAND_DESCRIPTION, FLAG_USAGE, RG_CLAIM_USAGE, RG_DELETE_USAGE,
            RG_INFO_EXT_USAGE, RG_ADDMEMBER_USAGE, RG_REMOVEMEMBER_USAGE;

    public static String getMessage(String key) {
        return messages.getString(key);
    }

    public static String getHost() {
        return config.getString("MySql.host");
    }

    public static int getPort() {
        return config.getInt("MySql.port");
    }

    public static String getDatabase() {
        return config.getString("MySql.database");
    }

    public static String getUsername() {
        return config.getString("MySql.username");
    }

    public static String getPassword() {
        return config.getString("MySql.password");
    }
}
