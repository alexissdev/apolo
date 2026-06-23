package dev.apolo.core.service.impl;

import dev.apolo.api.event.ApoloPrivateMessageEvent;
import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.model.PrivateMessageModel;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IMessageService;
import dev.apolo.api.service.IPrivateMessageService;
import dev.apolo.core.repository.interfaces.IPlayerStateRepository;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class PrivateMessageServiceImpl implements IPrivateMessageService {
    private final IPlayerStateRepository playerStateRepository;
    private final IMessageService messageService;
    private final Consumer<PrivateMessageModel> crossServerPublisher;

    @Override
    public ServiceResult<Void> sendMessage(Player from, String targetName, String message) {
        String fromUuid = from.getUniqueId().toString();

        String targetUuid = playerStateRepository.getPlayerUuidByName(targetName).orElse(null);
        if (targetUuid == null) {
            return ServiceResult.failure(MessageKey.MSG_PLAYER_NOT_ONLINE,
                Collections.singletonMap("player", targetName));
        }
        if (targetUuid.equals(fromUuid)) {
            return ServiceResult.failure(MessageKey.CANNOT_TARGET_SELF);
        }

        String resolvedTargetName = playerStateRepository.getPlayerNameByUuid(targetUuid)
            .orElse(targetName);

        PrivateMessageModel model = PrivateMessageModel.builder()
            .fromUuid(fromUuid)
            .fromName(from.getName())
            .toUuid(targetUuid)
            .toName(resolvedTargetName)
            .message(message)
            .timestamp(System.currentTimeMillis())
            .build();

        ApoloPrivateMessageEvent event = new ApoloPrivateMessageEvent(from, model);
        from.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return ServiceResult.failure(MessageKey.NO_PERMISSION);
        }

        // Registrar último interlocutor en ambas direcciones
        playerStateRepository.setLastMessageSender(targetUuid, fromUuid);
        playerStateRepository.setLastMessageSender(fromUuid, targetUuid);

        // Confirmar al emisor inmediatamente (está en este servidor)
        messageService.sendMessage(from, MessageKey.MSG_SENT,
            Map.of("player", resolvedTargetName, "message", message));

        // Publicar cross-server para que el servidor del receptor lo entregue
        crossServerPublisher.accept(model);
        return ServiceResult.success();
    }

    @Override
    public ServiceResult<Void> reply(Player from, String message) {
        String fromUuid = from.getUniqueId().toString();
        String lastSenderUuid = playerStateRepository.getLastMessageSender(fromUuid).orElse(null);
        if (lastSenderUuid == null) {
            return ServiceResult.failure(MessageKey.REPLY_NO_ONE);
        }

        String lastSenderName = playerStateRepository.getPlayerNameByUuid(lastSenderUuid)
            .orElse(null);
        if (lastSenderName == null) {
            return ServiceResult.failure(MessageKey.MSG_PLAYER_NOT_ONLINE,
                Collections.singletonMap("player", "?"));
        }

        return sendMessage(from, lastSenderName, message);
    }

    @Override
    public void deliverLocally(PrivateMessageModel model) {
        // Entregar al destinatario si está en ESTE servidor
        try {
            Player target = Bukkit.getPlayer(UUID.fromString(model.getToUuid()));
            if (target != null && target.isOnline()) {
                messageService.sendMessage(target, MessageKey.MSG_RECEIVED,
                    Map.of("player", model.getFromName(), "message", model.getMessage()));
                playerStateRepository.setLastMessageSender(model.getToUuid(), model.getFromUuid());
            }
        } catch (IllegalArgumentException ignored) {}

        // Notificar a jugadores con socialspy activo en este servidor
        Map<String, String> spyPlaceholders = Map.of(
            "from", model.getFromName(),
            "to", model.getToName(),
            "message", model.getMessage()
        );
        for (String spyUuid : playerStateRepository.getSocialSpyPlayers()) {
            if (spyUuid.equals(model.getFromUuid()) || spyUuid.equals(model.getToUuid())) continue;
            try {
                Player spy = Bukkit.getPlayer(UUID.fromString(spyUuid));
                if (spy != null && spy.isOnline()) {
                    messageService.sendMessage(spy, MessageKey.SOCIALSPY_FORMAT, spyPlaceholders);
                }
            } catch (IllegalArgumentException ignored) {}
        }
    }

    @Override
    public Set<String> getOnlinePlayerNames() {
        return playerStateRepository.getOnlinePlayerNames();
    }
}
