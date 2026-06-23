package dev.apolo.commands.impl;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.service.IMessageService;
import dev.apolo.commands.base.BaseCommand;
import dev.apolo.commands.base.CommandContext;
import dev.apolo.core.repository.interfaces.IPlayerStateRepository;

public class SocialSpyCommand extends BaseCommand {
    private final IPlayerStateRepository playerStateRepository;

    public SocialSpyCommand(IMessageService messageService, IPlayerStateRepository playerStateRepository) {
        super(messageService);
        this.playerStateRepository = playerStateRepository;
    }

    @Override
    protected void execute(CommandContext context) {
        if (!requirePlayer(context)) return;
        if (!requirePermission(context, "apolo.socialspy")) return;

        String uuid = context.getPlayer().getUniqueId().toString();
        boolean newState = !playerStateRepository.isSocialSpy(uuid);
        playerStateRepository.setSocialSpy(uuid, newState);

        messageService.sendMessage(context.getSender(),
            newState ? MessageKey.SOCIALSPY_ENABLED : MessageKey.SOCIALSPY_DISABLED);
    }
}
