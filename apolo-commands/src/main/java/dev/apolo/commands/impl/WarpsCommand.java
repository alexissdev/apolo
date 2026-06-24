package dev.apolo.commands.impl;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.model.WarpModel;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IMessageService;
import dev.apolo.api.service.IWarpService;
import dev.apolo.commands.base.BaseCommand;
import dev.apolo.commands.base.CommandContext;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

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

        boolean isPlayer = context.isPlayer();
        Player player = isPlayer ? context.getPlayer() : null;

        for (int i = start; i < end; i++) {
            WarpModel warp = warps.get(i);
            Map<String, String> placeholders = Map.of(
                "warp", warp.getName(),
                "world", warp.getWorldName(),
                "x", String.valueOf((int) warp.getX()),
                "y", String.valueOf((int) warp.getY()),
                "z", String.valueOf((int) warp.getZ())
            );

            if (isPlayer) {
                String text = messageService.getMessage(MessageKey.WARP_LIST_ENTRY, placeholders);
                TextComponent component = new TextComponent(text);
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/warp " + warp.getName()));
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder("§7Click para ir a §a" + warp.getName()).create()));
                player.spigot().sendMessage(component);
            } else {
                messageService.sendMessage(context.getSender(), MessageKey.WARP_LIST_ENTRY, placeholders);
            }
        }
        messageService.sendMessage(context.getSender(), MessageKey.WARP_LIST_FOOTER);
    }
}
