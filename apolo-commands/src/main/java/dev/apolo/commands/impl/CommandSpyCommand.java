package dev.apolo.commands.impl;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.service.IMessageService;
import dev.apolo.commands.base.BaseCommand;
import dev.apolo.commands.base.CommandContext;
import dev.apolo.core.repository.interfaces.IPlayerStateRepository;

public class CommandSpyCommand extends BaseCommand {
    private final IPlayerStateRepository playerStateRepository;

    public CommandSpyCommand(IMessageService messageService, IPlayerStateRepository playerStateRepository) {
        super(messageService);
        this.playerStateRepository = playerStateRepository;
    }

    @Override
    protected void execute(CommandContext context) {
        if (!requirePlayer(context)) return;
        if (!requirePermission(context, "apolo.commandspy")) return;

        String uuid = context.getPlayer().getUniqueId().toString();
        boolean newState = !playerStateRepository.isCommandSpy(uuid);
        playerStateRepository.setCommandSpy(uuid, newState);

        messageService.sendMessage(context.getSender(),
            newState ? MessageKey.COMMANDSPY_ENABLED : MessageKey.COMMANDSPY_DISABLED);
    }
}
