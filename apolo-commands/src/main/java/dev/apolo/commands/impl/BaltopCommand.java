package dev.apolo.commands.impl;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.model.UserModel;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IEconomyService;
import dev.apolo.api.service.IMessageService;
import dev.apolo.commands.base.BaseCommand;
import dev.apolo.commands.base.CommandContext;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BaltopCommand extends BaseCommand {
    private final IEconomyService economyService;
    private static final int DEFAULT_LIMIT = 10;

    public BaltopCommand(IMessageService messageService, IEconomyService economyService) {
        super(messageService);
        this.economyService = economyService;
    }

    @Override
    protected void execute(CommandContext context) {
        if (!requirePermission(context, "apolo.baltop")) return;

        int limit = DEFAULT_LIMIT;
        if (context.getArgs().length > 0) {
            try {
                limit = Integer.parseInt(context.getArgs()[0]);
                if (limit <= 0) limit = DEFAULT_LIMIT;
                if (limit > 100) limit = 100;
            } catch (NumberFormatException ignored) {}
        }

        ServiceResult<List<UserModel>> result = economyService.getTopBalances(limit);
        if (!result.isSuccess()) {
            result.getMessageKey().ifPresent(key ->
                messageService.sendMessage(context.getSender(), key, result.getPlaceholders()));
            return;
        }

        List<UserModel> top = result.getData().orElse(Collections.emptyList());
        messageService.sendMessage(context.getSender(), MessageKey.ECONOMY_TOP_HEADER,
            Collections.singletonMap("limit", String.valueOf(top.size())));

        for (int i = 0; i < top.size(); i++) {
            UserModel user = top.get(i);
            messageService.sendMessage(context.getSender(), MessageKey.ECONOMY_TOP_ENTRY,
                Map.of("position", String.valueOf(i + 1),
                    "player", user.getUsername(),
                    "balance", String.format("%.2f", user.getBalance())));
        }

        messageService.sendMessage(context.getSender(), MessageKey.ECONOMY_TOP_FOOTER);
    }

    @Override
    protected List<String> tabComplete(CommandContext context) {
        return Collections.emptyList();
    }
}
