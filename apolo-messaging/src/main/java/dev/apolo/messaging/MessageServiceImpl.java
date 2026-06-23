package dev.apolo.messaging;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.service.IMessageService;
import dev.apolo.messaging.formatter.ColorFormatter;
import dev.apolo.messaging.placeholder.PlaceholderResolver;
import dev.apolo.messaging.provider.MessageProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.Map;

public class MessageServiceImpl implements IMessageService {

    private final MessageProvider provider;
    private final PlaceholderResolver placeholderResolver;
    private final ColorFormatter colorFormatter;

    public MessageServiceImpl(MessageProvider provider, PlaceholderResolver placeholderResolver, ColorFormatter colorFormatter) {
        this.provider = provider;
        this.placeholderResolver = placeholderResolver;
        this.colorFormatter = colorFormatter;
    }

    @Override
    public String getMessage(MessageKey key) {
        return getMessage(key, Collections.emptyMap());
    }

    @Override
    public String getMessage(MessageKey key, Map<String, String> placeholders) {
        String raw = provider.getRaw(key);
        String prefix = provider.getRawByPath("prefix");
        raw = raw.replace("{prefix}", prefix);
        raw = placeholderResolver.resolve(raw, placeholders);
        return colorFormatter.format(raw);
    }

    @Override
    public void sendMessage(CommandSender sender, MessageKey key) {
        sender.sendMessage(getMessage(key));
    }

    @Override
    public void sendMessage(CommandSender sender, MessageKey key, Map<String, String> placeholders) {
        sender.sendMessage(getMessage(key, placeholders));
    }

    @Override
    public void broadcast(MessageKey key) {
        String message = getMessage(key);
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(message));
    }

    @Override
    public void reload() {
        provider.reload();
    }
}
