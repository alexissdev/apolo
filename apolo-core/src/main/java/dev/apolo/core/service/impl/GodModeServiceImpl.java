package dev.apolo.core.service.impl;

import dev.apolo.api.event.ApoloGodModeToggleEvent;
import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IGodModeService;
import dev.apolo.core.repository.interfaces.IPlayerStateRepository;
import dev.apolo.core.repository.interfaces.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class GodModeServiceImpl implements IGodModeService {
    private final IPlayerStateRepository playerStateRepository;
    private final IUserRepository userRepository;

    @Override
    public ServiceResult<Boolean> toggleGodMode(Player target) {
        boolean currentState = hasGodMode(target);
        boolean newState = !currentState;

        ApoloGodModeToggleEvent event = new ApoloGodModeToggleEvent(target, newState);
        target.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return ServiceResult.failure(MessageKey.NO_PERMISSION);
        }

        applyGodState(target, newState);
        return ServiceResult.success(newState);
    }

    @Override
    public ServiceResult<Void> enableGodMode(Player target) {
        ApoloGodModeToggleEvent event = new ApoloGodModeToggleEvent(target, true);
        target.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return ServiceResult.failure(MessageKey.NO_PERMISSION);
        }
        applyGodState(target, true);
        return ServiceResult.success();
    }

    @Override
    public ServiceResult<Void> disableGodMode(Player target) {
        ApoloGodModeToggleEvent event = new ApoloGodModeToggleEvent(target, false);
        target.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return ServiceResult.failure(MessageKey.NO_PERMISSION);
        }
        applyGodState(target, false);
        return ServiceResult.success();
    }

    @Override
    public boolean hasGodMode(Player player) {
        String uuid = player.getUniqueId().toString();
        if (playerStateRepository.hasGodState(uuid)) {
            return playerStateRepository.getGodState(uuid);
        }
        return false;
    }

    @Override
    public ServiceResult<Void> restoreState(Player player) {
        String uuid = player.getUniqueId().toString();
        userRepository.findByUuid(uuid).ifPresent(user ->
            playerStateRepository.setGodState(uuid, user.isGodModeEnabled())
        );
        return ServiceResult.success();
    }

    private void applyGodState(Player player, boolean enabled) {
        String uuid = player.getUniqueId().toString();
        playerStateRepository.setGodState(uuid, enabled);
        userRepository.updateGodState(uuid, enabled);
    }
}
