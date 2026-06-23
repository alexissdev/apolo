package dev.apolo.commands.base;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.service.IMessageService;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public abstract class BaseCommand implements CommandExecutor, TabCompleter {
    protected final IMessageService messageService;

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        execute(new CommandContext(sender, args));
        return true;
    }

    protected abstract void execute(CommandContext context);

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return tabComplete(new CommandContext(sender, args));
    }

    protected List<String> tabComplete(CommandContext context) {
        return Collections.emptyList();
    }

    protected boolean requirePlayer(CommandContext context) {
        if (!context.isPlayer()) {
            messageService.sendMessage(context.getSender(), MessageKey.ONLY_PLAYERS);
            return false;
        }
        return true;
    }

    protected boolean requirePermission(CommandContext context, String permission) {
        if (!context.getSender().hasPermission(permission)) {
            messageService.sendMessage(context.getSender(), MessageKey.NO_PERMISSION);
            return false;
        }
        return true;
    }

    protected Player findOnlinePlayer(CommandContext context, String name) {
        Player target = context.getSender().getServer().getPlayerExact(name);
        if (target == null) {
            messageService.sendMessage(context.getSender(), MessageKey.PLAYER_NOT_FOUND,
                java.util.Collections.singletonMap("player", name));
        }
        return target;
    }
}
