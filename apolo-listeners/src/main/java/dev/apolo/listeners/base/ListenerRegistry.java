package dev.apolo.listeners.base;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class ListenerRegistry {
    private final Plugin plugin;

    public ListenerRegistry(Plugin plugin) {
        this.plugin = plugin;
    }

    public void register(Listener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public void registerAll(List<Listener> listeners) {
        listeners.forEach(this::register);
    }
}
