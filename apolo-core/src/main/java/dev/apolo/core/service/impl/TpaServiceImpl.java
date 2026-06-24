package dev.apolo.core.service.impl;

import com.google.gson.Gson;
import dev.apolo.api.event.ApoloTpaAcceptEvent;
import dev.apolo.api.event.ApoloTpaDenyEvent;
import dev.apolo.api.event.ApoloTpaHereRequestEvent;
import dev.apolo.api.event.ApoloTpaRequestEvent;
import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.model.TpaRequestModel;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.ITpaService;
import dev.apolo.core.repository.interfaces.IPlayerStateRepository;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class TpaServiceImpl implements ITpaService {
    private final IPlayerStateRepository playerStateRepository;
    private final int tpaTimeout;
    private final int tpaCooldown;
    private final Gson gson = new Gson();

    @Override
    public ServiceResult<Void> sendRequest(Player from, Player target) {
        if (playerStateRepository.hasTpaCooldown(from.getUniqueId().toString())) {
            long remaining = playerStateRepository.getTpaCooldownTtl(from.getUniqueId().toString());
            return ServiceResult.cooldown(MessageKey.TPA_COOLDOWN,
                Collections.singletonMap("seconds", String.valueOf(remaining)));
        }

        TpaRequestModel request = TpaRequestModel.builder()
            .fromUuid(from.getUniqueId().toString())
            .fromName(from.getName())
            .targetUuid(target.getUniqueId().toString())
            .targetName(target.getName())
            .hereRequest(false)
            .createdAt(System.currentTimeMillis())
            .expiresAt(System.currentTimeMillis() + (tpaTimeout * 1000L))
            .build();

        ApoloTpaRequestEvent event = new ApoloTpaRequestEvent(from, target, request);
        from.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return ServiceResult.failure(MessageKey.NO_PERMISSION);
        }

        playerStateRepository.setTpaRequest(target.getUniqueId().toString(), gson.toJson(request), tpaTimeout);
        playerStateRepository.setTpaCooldown(from.getUniqueId().toString(), tpaCooldown);
        return ServiceResult.success();
    }

    @Override
    public ServiceResult<Void> sendHereRequest(Player from, Player target) {
        if (playerStateRepository.hasTpaCooldown(from.getUniqueId().toString())) {
            long remaining = playerStateRepository.getTpaCooldownTtl(from.getUniqueId().toString());
            return ServiceResult.cooldown(MessageKey.TPA_COOLDOWN,
                Collections.singletonMap("seconds", String.valueOf(remaining)));
        }

        TpaRequestModel request = TpaRequestModel.builder()
            .fromUuid(from.getUniqueId().toString())
            .fromName(from.getName())
            .targetUuid(target.getUniqueId().toString())
            .targetName(target.getName())
            .hereRequest(true)
            .createdAt(System.currentTimeMillis())
            .expiresAt(System.currentTimeMillis() + (tpaTimeout * 1000L))
            .build();

        ApoloTpaHereRequestEvent event = new ApoloTpaHereRequestEvent(from, target, request);
        from.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return ServiceResult.failure(MessageKey.NO_PERMISSION);
        }

        playerStateRepository.setTpaRequest(target.getUniqueId().toString(), gson.toJson(request), tpaTimeout);
        playerStateRepository.setTpaCooldown(from.getUniqueId().toString(), tpaCooldown);
        return ServiceResult.success();
    }

    @Override
    public ServiceResult<Void> accept(Player acceptor) {
        String json = playerStateRepository.getTpaRequest(acceptor.getUniqueId().toString());
        if (json == null) {
            return ServiceResult.failure(MessageKey.TPA_NO_PENDING);
        }

        TpaRequestModel request = gson.fromJson(json, TpaRequestModel.class);
        playerStateRepository.deleteTpaRequest(acceptor.getUniqueId().toString());

        Player from;
        try {
            from = acceptor.getServer().getPlayer(UUID.fromString(request.getFromUuid()));
        } catch (IllegalArgumentException e) {
            return ServiceResult.failure(MessageKey.PLAYER_OFFLINE,
                Collections.singletonMap("player", request.getFromName()));
        }
        if (from == null || !from.isOnline()) {
            return ServiceResult.failure(MessageKey.PLAYER_OFFLINE,
                Collections.singletonMap("player", request.getFromName()));
        }

        ApoloTpaAcceptEvent event = new ApoloTpaAcceptEvent(acceptor, request);
        acceptor.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return ServiceResult.failure(MessageKey.NO_PERMISSION);
        }

        if (request.isHereRequest()) {
            from.teleport(acceptor.getLocation());
        } else {
            acceptor.teleport(from.getLocation());
        }
        return ServiceResult.success();
    }

    @Override
    public ServiceResult<Void> deny(Player denier) {
        String json = playerStateRepository.getTpaRequest(denier.getUniqueId().toString());
        if (json == null) {
            return ServiceResult.failure(MessageKey.TPA_NO_PENDING);
        }

        TpaRequestModel request = gson.fromJson(json, TpaRequestModel.class);
        playerStateRepository.deleteTpaRequest(denier.getUniqueId().toString());

        ApoloTpaDenyEvent event = new ApoloTpaDenyEvent(denier, request);
        denier.getServer().getPluginManager().callEvent(event);
        return ServiceResult.success();
    }

    @Override
    public boolean hasPendingRequest(Player player) {
        return playerStateRepository.hasTpaRequest(player.getUniqueId().toString());
    }

    @Override
    public Optional<TpaRequestModel> getPendingRequest(Player player) {
        String json = playerStateRepository.getTpaRequest(player.getUniqueId().toString());
        if (json == null) return Optional.empty();
        return Optional.of(gson.fromJson(json, TpaRequestModel.class));
    }
}
