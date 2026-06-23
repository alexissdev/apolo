package dev.apolo.commands.impl;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IEconomyService;
import dev.apolo.api.service.IMessageService;
import dev.apolo.api.service.IUserService;
import dev.apolo.commands.base.BaseCommand;
import dev.apolo.commands.base.CommandContext;
import dev.apolo.commands.base.TabCompleterBase;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EcoCommand extends BaseCommand {
    private final IEconomyService economyService;
    private final IUserService userService;
    private static final List<String> SUBCOMMANDS = Arrays.asList("set", "give", "take");

    public EcoCommand(IMessageService messageService, IEconomyService economyService, IUserService userService) {
        super(messageService);
        this.economyService = economyService;
        this.userService = userService;
    }

    @Override
    protected void execute(CommandContext context) {
        if (!requirePermission(context, "apolo.eco")) return;

        if (context.getArgs().length < 3) {
            messageService.sendMessage(context.getSender(), MessageKey.INVALID_ARGUMENTS);
            return;
        }

        String subCommand = context.getArgs()[0].toLowerCase();
        String targetName = context.getArgs()[1];
        double amount;

        try {
            amount = Double.parseDouble(context.getArgs()[2]);
        } catch (NumberFormatException e) {
            messageService.sendMessage(context.getSender(), MessageKey.ECONOMY_INVALID_AMOUNT);
            return;
        }

        if (amount <= 0) {
            messageService.sendMessage(context.getSender(), MessageKey.ECONOMY_INVALID_AMOUNT);
            return;
        }

        Player onlineTarget = context.getSender().getServer().getPlayerExact(targetName);
        String targetUuid = null;
        String resolvedName = targetName;

        if (onlineTarget != null) {
            targetUuid = onlineTarget.getUniqueId().toString();
            resolvedName = onlineTarget.getName();
        } else {
            messageService.sendMessage(context.getSender(), MessageKey.PLAYER_NOT_FOUND,
                Collections.singletonMap("player", targetName));
            return;
        }

        ServiceResult<Void> result;
        switch (subCommand) {
            case "set":
                result = economyService.setBalance(targetUuid, amount);
                if (result.isSuccess()) {
                    messageService.sendMessage(context.getSender(), MessageKey.ECONOMY_SET_BALANCE_SUCCESS,
                        Map.of("player", resolvedName, "amount", String.format("%.2f", amount)));
                } else {
                    result.getMessageKey().ifPresent(key ->
                        messageService.sendMessage(context.getSender(), key, result.getPlaceholders()));
                }
                break;
            case "give":
                result = economyService.deposit(targetUuid, amount, "Admin deposit");
                if (result.isSuccess()) {
                    messageService.sendMessage(context.getSender(), MessageKey.ECONOMY_ADMIN_DEPOSIT_SUCCESS,
                        Map.of("player", resolvedName, "amount", String.format("%.2f", amount)));
                } else {
                    result.getMessageKey().ifPresent(key ->
                        messageService.sendMessage(context.getSender(), key, result.getPlaceholders()));
                }
                break;
            case "take":
                result = economyService.withdraw(targetUuid, amount, "Admin withdrawal");
                if (result.isSuccess()) {
                    messageService.sendMessage(context.getSender(), MessageKey.ECONOMY_ADMIN_WITHDRAW_SUCCESS,
                        Map.of("player", resolvedName, "amount", String.format("%.2f", amount)));
                } else {
                    result.getMessageKey().ifPresent(key ->
                        messageService.sendMessage(context.getSender(), key, result.getPlaceholders()));
                }
                break;
            default:
                messageService.sendMessage(context.getSender(), MessageKey.INVALID_ARGUMENTS);
                break;
        }
    }

    @Override
    protected List<String> tabComplete(CommandContext context) {
        if (context.getArgs().length == 1) {
            return TabCompleterBase.filter(SUBCOMMANDS, context.getArgs()[0]);
        }
        if (context.getArgs().length == 2) {
            return null;
        }
        return Collections.emptyList();
    }
}
