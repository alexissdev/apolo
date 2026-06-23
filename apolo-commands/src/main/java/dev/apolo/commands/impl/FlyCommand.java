package dev.apolo.commands.impl;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IFlyService;
import dev.apolo.api.service.IMessageService;
import dev.apolo.commands.base.BaseCommand;
import dev.apolo.commands.base.CommandContext;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FlyCommand extends BaseCommand {
    private final IFlyService flyService;

    public FlyCommand(IMessageService messageService, IFlyService flyService) {
        super(messageService);
        this.flyService = flyService;
    }

    @Override
    protected void execute(CommandContext context) {
        if (!requirePermission(context, "apolo.fly")) return;

        if (context.getArgs().length >= 1) {
            if (!requirePermission(context, "apolo.fly.others")) return;
            Player target = findOnlinePlayer(context, context.getArgs()[0]);
            if (target == null) return;

            ServiceResult<Boolean> result = flyService.toggleFly(target);
            if (result.isSuccess()) {
                boolean enabled = result.getData().orElse(false);
                if (enabled) {
                    messageService.sendMessage(context.getSender(), MessageKey.FLY_ENABLED_OTHER_SENDER,
                        Collections.singletonMap("player", target.getName()));
                    messageService.sendMessage(target, MessageKey.FLY_ENABLED_OTHER_TARGET,
                        Collections.singletonMap("player", context.getSender().getName()));
                } else {
                    messageService.sendMessage(context.getSender(), MessageKey.FLY_DISABLED_OTHER_SENDER,
                        Collections.singletonMap("player", target.getName()));
                    messageService.sendMessage(target, MessageKey.FLY_DISABLED_OTHER_TARGET,
                        Collections.singletonMap("player", context.getSender().getName()));
                }
            } else {
                result.getMessageKey().ifPresent(key ->
                    messageService.sendMessage(context.getSender(), key, result.getPlaceholders()));
            }
        } else {
            if (!requirePlayer(context)) return;
            Player player = context.getPlayer();

            ServiceResult<Boolean> result = flyService.toggleFly(player);
            if (result.isSuccess()) {
                boolean enabled = result.getData().orElse(false);
                if (enabled) {
                    messageService.sendMessage(player, MessageKey.FLY_ENABLED_SELF);
                } else {
                    messageService.sendMessage(player, MessageKey.FLY_DISABLED_SELF);
                }
            } else {
                result.getMessageKey().ifPresent(key ->
                    messageService.sendMessage(player, key, result.getPlaceholders()));
            }
        }
    }

    @Override
    protected List<String> tabComplete(CommandContext context) {
        if (context.getArgs().length == 1 && context.getSender().hasPermission("apolo.fly.others")) {
            return null;
        }
        return Collections.emptyList();
    }
}
