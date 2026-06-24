package dev.apolo.core.service.impl;

import dev.apolo.api.event.ApoloFlyToggleEvent;
import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IFlyService;
import dev.apolo.core.repository.interfaces.IPlayerStateRepository;
import dev.apolo.core.repository.interfaces.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class FlyServiceImpl implements IFlyService {
    private final IPlayerStateRepository playerStateRepository;
    private final IUserRepository userRepository;

    @Override
    public ServiceResult<Boolean> toggleFly(Player target) {
        boolean currentState = hasFly(target);
        boolean newState = !currentState;

        ApoloFlyToggleEvent event = new ApoloFlyToggleEvent(target, newState);
        target.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return ServiceResult.failure(MessageKey.NO_PERMISSION);
        }

        applyFlyState(target, newState);
        return ServiceResult.success(newState);
    }

    @Override
    public ServiceResult<Void> enableFly(Player target) {
        ApoloFlyToggleEvent event = new ApoloFlyToggleEvent(target, true);
        target.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return ServiceResult.failure(MessageKey.NO_PERMISSION);
        }
        applyFlyState(target, true);
        return ServiceResult.success();
    }

    @Override
    public ServiceResult<Void> disableFly(Player target) {
        ApoloFlyToggleEvent event = new ApoloFlyToggleEvent(target, false);
        target.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return ServiceResult.failure(MessageKey.NO_PERMISSION);
        }
        applyFlyState(target, false);
        return ServiceResult.success();
    }

    @Override
    public boolean hasFly(Player player) {
        String uuid = player.getUniqueId().toString();
        if (playerStateRepository.hasFlyState(uuid)) {
            return playerStateRepository.getFlyState(uuid);
        }
        return player.getAllowFlight();
    }

    @Override
    public ServiceResult<Void> restoreState(Player player) {
        String uuid = player.getUniqueId().toString();
        userRepository.findByUuid(uuid).ifPresent(user -> {
            if (user.isFlyEnabled()) {
                Bukkit.getScheduler().runTask(
                    Bukkit.getPluginManager().getPlugin("Apolo"),
                    () -> applyFlyState(player, true)
                );
            }
            playerStateRepository.setFlyState(uuid, user.isFlyEnabled());
        });
        return ServiceResult.success();
    }

    private void applyFlyState(Player player, boolean enabled) {
        String uuid = player.getUniqueId().toString();
        if (enabled) {
            player.setAllowFlight(true);
            player.setFlying(true);
        } else {
            if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
                player.setFlying(false);
                player.setAllowFlight(false);
            }
        }
        playerStateRepository.setFlyState(uuid, enabled);
        userRepository.updateFlyState(uuid, enabled);
    }
}
