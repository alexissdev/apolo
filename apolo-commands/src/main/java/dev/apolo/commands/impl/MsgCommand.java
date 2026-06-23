package dev.apolo.commands.impl;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IMessageService;
import dev.apolo.api.service.IPrivateMessageService;
import dev.apolo.commands.base.BaseCommand;
import dev.apolo.commands.base.CommandContext;
import dev.apolo.commands.base.TabCompleterBase;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MsgCommand extends BaseCommand {
    private final IPrivateMessageService privateMessageService;

    public MsgCommand(IMessageService messageService, IPrivateMessageService privateMessageService) {
        super(messageService);
        this.privateMessageService = privateMessageService;
    }

    @Override
    protected void execute(CommandContext context) {
        if (!requirePlayer(context)) return;
        if (!requirePermission(context, "apolo.msg")) return;

        if (context.getArgs().length < 2) {
            messageService.sendMessage(context.getSender(), MessageKey.MSG_USAGE);
            return;
        }

        String targetName = context.getArgs()[0];
        String message = Arrays.stream(context.getArgs())
            .skip(1)
            .collect(Collectors.joining(" "));

        ServiceResult<Void> result = privateMessageService.sendMessage(
            context.getPlayer(), targetName, message);

        if (!result.isSuccess()) {
            result.getMessageKey().ifPresent(key ->
                messageService.sendMessage(context.getSender(), key, result.getPlaceholders()));
        }
    }

    @Override
    protected List<String> tabComplete(CommandContext context) {
        if (context.getArgs().length == 1) {
            List<String> names = privateMessageService.getOnlinePlayerNames()
                .stream()
                .filter(name -> !name.equalsIgnoreCase(context.getSender().getName()))
                .collect(Collectors.toList());
            return TabCompleterBase.filter(names, context.getArgs()[0]);
        }
        return Collections.emptyList();
    }
}
