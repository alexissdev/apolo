package dev.apolo.commands.impl;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IMessageService;
import dev.apolo.api.service.ITpaService;
import dev.apolo.commands.base.BaseCommand;
import dev.apolo.commands.base.CommandContext;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TpHereCommand extends BaseCommand {
    private final ITpaService tpaService;

    public TpHereCommand(IMessageService messageService, ITpaService tpaService) {
        super(messageService);
        this.tpaService = tpaService;
    }

    @Override
    protected void execute(CommandContext context) {
        if (!requirePlayer(context)) return;
        if (!requirePermission(context, "apolo.tphere")) return;

        if (context.getArgs().length == 0) {
            messageService.sendMessage(context.getSender(), MessageKey.TPHERE_USAGE);
            return;
        }

        Player sender = context.getPlayer();
        Player target = findOnlinePlayer(context, context.getArgs()[0]);
        if (target == null) return;

        if (sender.equals(target)) {
            messageService.sendMessage(sender, MessageKey.CANNOT_TARGET_SELF);
            return;
        }

        ServiceResult<Void> result = tpaService.sendHereRequest(sender, target);

        if (result.isSuccess()) {
            messageService.sendMessage(sender, MessageKey.TPHERE_REQUEST_SENT,
                Collections.singletonMap("player", target.getName()));

            String receivedMsg = messageService.getMessage(MessageKey.TPHERE_REQUEST_RECEIVED,
                Collections.singletonMap("player", sender.getName()));

            TextComponent message = new TextComponent(receivedMsg + " ");
            TextComponent accept = new TextComponent("§a[Aceptar]");
            TextComponent deny = new TextComponent(" §c[Rechazar]");
            accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
            deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpadeny"));
            message.addExtra(accept);
            message.addExtra(deny);
            target.spigot().sendMessage(message);
        } else {
            result.getMessageKey().ifPresent(key ->
                messageService.sendMessage(sender, key, result.getPlaceholders()));
        }
    }

    @Override
    protected List<String> tabComplete(CommandContext context) {
        return null;
    }
}
