package com.rustret.worldguard.configurable;

import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.Config;

public class PluginConfig {
    private static Config translations, config;

    public static void save(Plugin plugin) {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();

        plugin.saveResource("translations.yml");
        translations = new Config(plugin.getDataFolder() + "/translations.yml");
    }

    public static String getTranslation(String key) {
        return translations.getString(key);
    }

    public static Object getValue(String key) {
        return config.get(key);
    }
}
