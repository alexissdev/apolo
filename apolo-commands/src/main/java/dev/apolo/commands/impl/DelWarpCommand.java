package dev.apolo.commands.impl;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.model.WarpModel;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IMessageService;
import dev.apolo.api.service.IWarpService;
import dev.apolo.commands.base.BaseCommand;
import dev.apolo.commands.base.CommandContext;
import dev.apolo.commands.base.TabCompleterBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DelWarpCommand extends BaseCommand {
    private final IWarpService warpService;

    public DelWarpCommand(IMessageService messageService, IWarpService warpService) {
        super(messageService);
        this.warpService = warpService;
    }

    @Override
    protected void execute(CommandContext context) {
        if (!requirePermission(context, "apolo.warp.delete")) return;

        if (context.getArgs().length == 0) {
            messageService.sendMessage(context.getSender(), MessageKey.WARP_DELWARP_USAGE);
            return;
        }

        String warpName = context.getArgs()[0];
        ServiceResult<Void> result = warpService.deleteWarp(warpName);
        if (result.isSuccess()) {
            messageService.sendMessage(context.getSender(), MessageKey.WARP_DELETED,
                Collections.singletonMap("warp", warpName));
        } else {
            result.getMessageKey().ifPresent(key ->
                messageService.sendMessage(context.getSender(), key, result.getPlaceholders()));
        }
    }

    @Override
    protected List<String> tabComplete(CommandContext context) {
        if (context.getArgs().length == 1 && context.getSender().hasPermission("apolo.warp.delete")) {
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
