package dev.apolo.commands.impl;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.model.WarpModel;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IMessageService;
import dev.apolo.api.service.IWarpService;
import dev.apolo.commands.base.BaseCommand;
import dev.apolo.commands.base.CommandContext;
import dev.apolo.commands.base.TabCompleterBase;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WarpCommand extends BaseCommand {
    private final IWarpService warpService;

    public WarpCommand(IMessageService messageService, IWarpService warpService) {
        super(messageService);
        this.warpService = warpService;
    }

    @Override
    protected void execute(CommandContext context) {
        if (!requirePlayer(context)) return;
        if (!requirePermission(context, "apolo.warp")) return;

        if (context.getArgs().length == 0) {
            messageService.sendMessage(context.getSender(), MessageKey.WARP_USAGE);
            return;
        }

        Player player = context.getPlayer();
        String warpName = context.getArgs()[0];
        messageService.sendMessage(player, MessageKey.WARP_TELEPORTING,
            Collections.singletonMap("warp", warpName));
        ServiceResult<Void> result = warpService.teleportToWarp(player, warpName);
        if (!result.isSuccess()) {
            result.getMessageKey().ifPresent(key ->
                messageService.sendMessage(player, key, result.getPlaceholders()));
        }
    }

    @Override
    protected List<String> tabComplete(CommandContext context) {
        if (context.getArgs().length == 1) {
            ServiceResult<List<WarpModel>> allWarps = warpService.getAllWarps();
            if (allWarps.isSuccess()) {
                List<String> names = new ArrayList<>();
                allWarps.getData().ifPresent(warps -> warps.forEach(w -> names.add(w.getName())));
                return TabCompleterBase.filter(names, context.getArgs()[0]);
            }
        }
        return Collections.emptyList();
    }
}
