package dev.apolo.plugin.config;

import org.bukkit.plugin.Plugin;

public class ConfigLoader {
    private final Plugin plugin;

    public ConfigLoader(Plugin plugin) {
        this.plugin = plugin;
    }

    public ApoloConfig load() {
        plugin.saveDefaultConfig();
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
        ApoloConfig config = new ApoloConfig();
        config.load(plugin);
        return config;
    }
}
