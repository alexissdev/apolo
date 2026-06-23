package dev.apolo.commands.impl;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IMessageService;
import dev.apolo.api.service.IWarpService;
import dev.apolo.commands.base.BaseCommand;
import dev.apolo.commands.base.CommandContext;
import org.bukkit.entity.Player;

import java.util.Collections;

public class SetWarpCommand extends BaseCommand {
    private final IWarpService warpService;

    public SetWarpCommand(IMessageService messageService, IWarpService warpService) {
        super(messageService);
        this.warpService = warpService;
    }

    @Override
    protected void execute(CommandContext context) {
        if (!requirePlayer(context)) return;
        if (!requirePermission(context, "apolo.warp.set")) return;

        if (context.getArgs().length == 0) {
            messageService.sendMessage(context.getSender(), MessageKey.WARP_SETWARP_USAGE);
            return;
        }

        Player player = context.getPlayer();
        String warpName = context.getArgs()[0];
        ServiceResult<Void> result = warpService.createWarp(warpName, player.getLocation(), player);
        if (result.isSuccess()) {
            messageService.sendMessage(player, MessageKey.WARP_CREATED,
                Collections.singletonMap("warp", warpName));
        } else {
            result.getMessageKey().ifPresent(key ->
                messageService.sendMessage(player, key, result.getPlaceholders()));
        }
    }
}
