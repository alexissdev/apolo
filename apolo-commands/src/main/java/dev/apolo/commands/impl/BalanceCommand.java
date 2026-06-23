package dev.apolo.commands.impl;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IEconomyService;
import dev.apolo.api.service.IMessageService;
import dev.apolo.api.service.IUserService;
import dev.apolo.commands.base.BaseCommand;
import dev.apolo.commands.base.CommandContext;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BalanceCommand extends BaseCommand {
    private final IEconomyService economyService;
    private final IUserService userService;

    public BalanceCommand(IMessageService messageService, IEconomyService economyService, IUserService userService) {
        super(messageService);
        this.economyService = economyService;
        this.userService = userService;
    }

    @Override
    protected void execute(CommandContext context) {
        if (!requirePermission(context, "apolo.balance")) return;

        if (context.getArgs().length == 0) {
            if (!requirePlayer(context)) return;
            Player player = context.getPlayer();
            ServiceResult<Double> result = economyService.getBalance(player);
            if (result.isSuccess()) {
                messageService.sendMessage(player, MessageKey.ECONOMY_BALANCE_SELF,
                    Collections.singletonMap("balance", String.format("%.2f", result.getData().orElse(0.0))));
            } else {
                result.getMessageKey().ifPresent(key ->
                    messageService.sendMessage(player, key, result.getPlaceholders()));
            }
        } else {
            if (!requirePermission(context, "apolo.balance.others")) return;
            String targetName = context.getArgs()[0];

            Player onlineTarget = context.getSender().getServer().getPlayerExact(targetName);
            if (onlineTarget != null) {
                ServiceResult<Double> result = economyService.getBalance(onlineTarget);
                if (result.isSuccess()) {
                    messageService.sendMessage(context.getSender(), MessageKey.ECONOMY_BALANCE_OTHER,
                        Map.of("player", onlineTarget.getName(),
                            "balance", String.format("%.2f", result.getData().orElse(0.0))));
                } else {
                    result.getMessageKey().ifPresent(key ->
                        messageService.sendMessage(context.getSender(), key, result.getPlaceholders()));
                }
                return;
            }

            messageService.sendMessage(context.getSender(), MessageKey.PLAYER_NOT_FOUND,
                Collections.singletonMap("player", targetName));
        }
    }

    @Override
    protected List<String> tabComplete(CommandContext context) {
        if (context.getArgs().length == 1 && context.getSender().hasPermission("apolo.balance.others")) {
            return null;
        }
        return Collections.emptyList();
    }
}
