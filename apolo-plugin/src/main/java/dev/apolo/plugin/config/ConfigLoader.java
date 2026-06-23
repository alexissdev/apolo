package dev.apolo.plugin.config;

import org.bukkit.plugin.Plugin;

public class ConfigLoader {
    private final Plugin plugin;

    public ConfigLoader(Plugin plugin) {
        this.plugin = plugin;
    }

    public ApoloConfig load() {
        plugin.saveDefaultConfig();
        ApoloConfig config = new ApoloConfig();
        config.load(plugin);
        return config;
    }
}
