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

public class TpDenyCommand extends BaseCommand {
    private final ITpaService tpaService;

    public TpDenyCommand(IMessageService messageService, ITpaService tpaService) {
        super(messageService);
        this.tpaService = tpaService;
    }

    @Override
    protected void execute(CommandContext context) {
        if (!requirePlayer(context)) return;
        Player denier = context.getPlayer();

        Optional<TpaRequestModel> requestOpt = tpaService.getPendingRequest(denier);
        if (requestOpt.isEmpty()) {
            messageService.sendMessage(denier, MessageKey.TPA_NO_PENDING);
            return;
        }

        TpaRequestModel request = requestOpt.get();
        ServiceResult<Void> result = tpaService.deny(denier);

        if (result.isSuccess()) {
            messageService.sendMessage(denier, MessageKey.TPA_DENIED_TARGET,
                Collections.singletonMap("player", request.getFromName()));
            Player from = denier.getServer().getPlayer(UUID.fromString(request.getFromUuid()));
            if (from != null) {
                messageService.sendMessage(from, MessageKey.TPA_DENIED_SENDER,
                    Collections.singletonMap("player", denier.getName()));
            }
        }
    }
}
