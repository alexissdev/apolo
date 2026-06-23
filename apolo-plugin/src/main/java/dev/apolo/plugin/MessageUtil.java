package dev.apolo.plugin;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IMessageService;
import org.bukkit.command.CommandSender;

public final class MessageUtil {
    private MessageUtil() {}

    public static <T> void sendResult(CommandSender sender, ServiceResult<T> result, IMessageService messageService) {
        if (!result.isSuccess()) {
            result.getMessageKey().ifPresent(key ->
                messageService.sendMessage(sender, key, result.getPlaceholders()));
        }
    }
}
