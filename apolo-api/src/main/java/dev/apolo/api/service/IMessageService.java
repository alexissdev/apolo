package dev.apolo.api.service;

import dev.apolo.api.messaging.MessageKey;
import org.bukkit.command.CommandSender;

import java.util.Map;

public interface IMessageService {
    String getMessage(MessageKey key);
    String getMessage(MessageKey key, Map<String, String> placeholders);
    void sendMessage(CommandSender sender, MessageKey key);
    void sendMessage(CommandSender sender, MessageKey key, Map<String, String> placeholders);
    void broadcast(MessageKey key);
    void reload();
}
