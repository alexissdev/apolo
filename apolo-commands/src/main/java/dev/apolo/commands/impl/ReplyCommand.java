package dev.apolo.commands.impl;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IMessageService;
import dev.apolo.api.service.IPrivateMessageService;
import dev.apolo.commands.base.BaseCommand;
import dev.apolo.commands.base.CommandContext;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ReplyCommand extends BaseCommand {
    private final IPrivateMessageService privateMessageService;

    public ReplyCommand(IMessageService messageService, IPrivateMessageService privateMessageService) {
        super(messageService);
        this.privateMessageService = privateMessageService;
    }

    @Override
    protected void execute(CommandContext context) {
        if (!requirePlayer(context)) return;
        if (!requirePermission(context, "apolo.msg")) return;

        if (context.getArgs().length == 0) {
            messageService.sendMessage(context.getSender(), MessageKey.REPLY_USAGE);
            return;
        }

        String message = Arrays.stream(context.getArgs())
            .collect(Collectors.joining(" "));

        ServiceResult<Void> result = privateMessageService.reply(context.getPlayer(), message);

        if (!result.isSuccess()) {
            result.getMessageKey().ifPresent(key ->
                messageService.sendMessage(context.getSender(), key, result.getPlaceholders()));
        }
    }
}
