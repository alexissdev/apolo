package dev.apolo.commands.impl;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.model.WarpModel;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IMessageService;
import dev.apolo.api.service.IWarpService;
import dev.apolo.commands.base.BaseCommand;
import dev.apolo.commands.base.CommandContext;

import java.util.List;
import java.util.Map;

public class WarpsCommand extends BaseCommand {
    private final IWarpService warpService;
    private final int warpsPerPage;

    public WarpsCommand(IMessageService messageService, IWarpService warpService, int warpsPerPage) {
        super(messageService);
        this.warpService = warpService;
        this.warpsPerPage = warpsPerPage;
    }

    @Override
    protected void execute(CommandContext context) {
        if (!requirePermission(context, "apolo.warp")) return;

        ServiceResult<List<WarpModel>> result = warpService.getAllWarps();
        if (!result.isSuccess() || result.getData().map(List::isEmpty).orElse(true)) {
            messageService.sendMessage(context.getSender(), MessageKey.WARP_LIST_EMPTY);
            return;
        }

        List<WarpModel> warps = result.getData().get();
        int page = 1;
        if (context.getArgs().length > 0) {
            try { page = Integer.parseInt(context.getArgs()[0]); } catch (NumberFormatException ignored) {}
        }

        int totalPages = (int) Math.ceil((double) warps.size() / warpsPerPage);
        page = Math.max(1, Math.min(page, totalPages));
        int start = (page - 1) * warpsPerPage;
        int end = Math.min(start + warpsPerPage, warps.size());

        messageService.sendMessage(context.getSender(), MessageKey.WARP_LIST_HEADER,
            Map.of("page", String.valueOf(page), "total_pages", String.valueOf(totalPages)));

        for (int i = start; i < end; i++) {
            WarpModel warp = warps.get(i);
            messageService.sendMessage(context.getSender(), MessageKey.WARP_LIST_ENTRY,
                Map.of("warp", warp.getName(), "world", warp.getWorldName(),
                    "x", String.valueOf((int) warp.getX()),
                    "y", String.valueOf((int) warp.getY()),
                    "z", String.valueOf((int) warp.getZ())));
        }
        messageService.sendMessage(context.getSender(), MessageKey.WARP_LIST_FOOTER);
    }
}
