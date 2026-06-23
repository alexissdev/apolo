package dev.apolo.commands.base;

import lombok.Value;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Value
public class CommandContext {
    CommandSender sender;
    String[] args;

    public boolean isPlayer() {
        return sender instanceof Player;
    }

    public Player getPlayer() {
        return (Player) sender;
    }
}
