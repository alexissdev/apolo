package dev.apolo.commands.impl;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IMessageService;
import dev.apolo.api.service.ITpaService;
import dev.apolo.commands.base.BaseCommand;
import dev.apolo.commands.base.CommandContext;
import org.bukkit.entity.Player;

import java.util.Collections;

public class TpaCancelCommand extends BaseCommand {
    private final ITpaService tpaService;

    public TpaCancelCommand(IMessageService messageService, ITpaService tpaService) {
        super(messageService);
        this.tpaService = tpaService;
    }

    @Override
    protected void execute(CommandContext context) {
        if (!requirePlayer(context)) return;
        Player sender = context.getPlayer();

        ServiceResult<String> result = tpaService.cancel(sender);

        if (result.isSuccess()) {
            String targetName = result.getData().orElse("?");
            messageService.sendMessage(sender, MessageKey.TPA_CANCELLED_SENDER,
                Collections.singletonMap("player", targetName));

            Player target = sender.getServer().getPlayerExact(targetName);
            if (target != null && target.isOnline()) {
                messageService.sendMessage(target, MessageKey.TPA_CANCELLED_TARGET,
                    Collections.singletonMap("player", sender.getName()));
            }
        } else {
            result.getMessageKey().ifPresent(key ->
                messageService.sendMessage(sender, key, result.getPlaceholders()));
        }
    }
}
