package dev.apolo.commands.impl;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IEconomyService;
import dev.apolo.api.service.IMessageService;
import dev.apolo.commands.base.BaseCommand;
import dev.apolo.commands.base.CommandContext;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PayCommand extends BaseCommand {
    private final IEconomyService economyService;

    public PayCommand(IMessageService messageService, IEconomyService economyService) {
        super(messageService);
        this.economyService = economyService;
    }

    @Override
    protected void execute(CommandContext context) {
        if (!requirePlayer(context)) return;
        if (!requirePermission(context, "apolo.pay")) return;

        if (context.getArgs().length < 2) {
            messageService.sendMessage(context.getSender(), MessageKey.ECONOMY_TRANSFER_USAGE);
            return;
        }

        Player sender = context.getPlayer();
        Player target = findOnlinePlayer(context, context.getArgs()[0]);
        if (target == null) return;

        if (sender.equals(target)) {
            messageService.sendMessage(sender, MessageKey.CANNOT_TARGET_SELF);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(context.getArgs()[1]);
        } catch (NumberFormatException e) {
            messageService.sendMessage(sender, MessageKey.ECONOMY_INVALID_AMOUNT);
            return;
        }

        if (amount <= 0) {
            messageService.sendMessage(sender, MessageKey.ECONOMY_INVALID_AMOUNT);
            return;
        }

        String fromUuid = sender.getUniqueId().toString();
        String toUuid = target.getUniqueId().toString();
        String reason = "Transfer from " + sender.getName() + " to " + target.getName();

        ServiceResult<Void> result = economyService.transfer(fromUuid, toUuid, amount, reason);
        if (result.isSuccess()) {
            messageService.sendMessage(sender, MessageKey.ECONOMY_TRANSFER_SENT,
                Map.of("player", target.getName(), "amount", String.format("%.2f", amount)));
            messageService.sendMessage(target, MessageKey.ECONOMY_TRANSFER_RECEIVED,
                Map.of("player", sender.getName(), "amount", String.format("%.2f", amount)));
        } else {
            result.getMessageKey().ifPresent(key ->
                messageService.sendMessage(sender, key, result.getPlaceholders()));
        }
    }

    @Override
    protected List<String> tabComplete(CommandContext context) {
        if (context.getArgs().length == 1) {
            return null;
        }
        return Collections.emptyList();
    }
}
