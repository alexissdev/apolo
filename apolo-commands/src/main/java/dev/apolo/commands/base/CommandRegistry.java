package dev.apolo.commands.base;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class CommandRegistry {
    private final Plugin plugin;

    public CommandRegistry(Plugin plugin) {
        this.plugin = plugin;
    }

    public void register(String name, BaseCommand command) {
        PluginCommand pluginCommand = plugin.getCommand(name);
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
        }
    }

    public void registerAll(Map<String, BaseCommand> commands) {
        commands.forEach(this::register);
    }
}
