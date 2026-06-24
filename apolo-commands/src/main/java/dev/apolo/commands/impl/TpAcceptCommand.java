package dev.apolo.commands.impl;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.model.TpaRequestModel;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IMessageService;
import dev.apolo.api.service.ITpaService;
import dev.apolo.commands.base.BaseCommand;
import dev.apolo.commands.base.CommandContext;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class TpAcceptCommand extends BaseCommand {
    private final ITpaService tpaService;

    public TpAcceptCommand(IMessageService messageService, ITpaService tpaService) {
        super(messageService);
        this.tpaService = tpaService;
    }

    @Override
    protected void execute(CommandContext context) {
        if (!requirePlayer(context)) return;
        Player acceptor = context.getPlayer();

        Optional<TpaRequestModel> requestOpt = tpaService.getPendingRequest(acceptor);
        if (requestOpt.isEmpty()) {
            messageService.sendMessage(acceptor, MessageKey.TPA_NO_PENDING);
            return;
        }

        TpaRequestModel request = requestOpt.get();
        ServiceResult<Void> result = tpaService.accept(acceptor);

        if (result.isSuccess()) {
            Player from = null;
            try {
                from = acceptor.getServer().getPlayer(UUID.fromString(request.getFromUuid()));
            } catch (IllegalArgumentException ignored) {}

            boolean isHere = request.isHereRequest();
            if (isHere) {
                messageService.sendMessage(acceptor, MessageKey.TPHERE_ACCEPTED_TARGET,
                    Collections.singletonMap("player", request.getFromName()));
                if (from != null) {
                    messageService.sendMessage(from, MessageKey.TPHERE_ACCEPTED_SENDER,
                        Collections.singletonMap("player", acceptor.getName()));
                }
            } else {
                messageService.sendMessage(acceptor, MessageKey.TPA_ACCEPTED_TARGET,
                    Collections.singletonMap("player", request.getFromName()));
                if (from != null) {
                    messageService.sendMessage(from, MessageKey.TPA_ACCEPTED_SENDER,
                        Collections.singletonMap("player", acceptor.getName()));
                }
                messageService.sendMessage(acceptor, MessageKey.TPA_TELEPORTING);
            }
        } else {
            result.getMessageKey().ifPresent(key ->
                messageService.sendMessage(acceptor, key, result.getPlaceholders()));
        }
    }
}
