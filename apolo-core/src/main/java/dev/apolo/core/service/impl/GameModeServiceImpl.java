package dev.apolo.core.service.impl;

import dev.apolo.api.event.ApoloGameModeChangeEvent;
import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IGameModeService;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class GameModeServiceImpl implements IGameModeService {

    @Override
    public ServiceResult<Void> setGameMode(Player target, GameMode mode) {
        GameMode oldMode = target.getGameMode();
        ApoloGameModeChangeEvent event = new ApoloGameModeChangeEvent(target, oldMode, mode);
        target.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return ServiceResult.failure(MessageKey.NO_PERMISSION);
        }
        target.setGameMode(mode);
        return ServiceResult.success();
    }

    @Override
    public GameMode parseGameMode(String input) {
        switch (input.toLowerCase()) {
            case "survival": case "s": case "0": return GameMode.SURVIVAL;
            case "creative": case "c": case "1": return GameMode.CREATIVE;
            case "adventure": case "a": case "2": return GameMode.ADVENTURE;
            case "spectator": case "sp": case "3": return GameMode.SPECTATOR;
            default: return null;
        }
    }
}
