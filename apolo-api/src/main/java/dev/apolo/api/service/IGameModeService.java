package dev.apolo.api.service;

import dev.apolo.api.result.ServiceResult;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public interface IGameModeService {
    ServiceResult<Void> setGameMode(Player target, GameMode mode);
    GameMode parseGameMode(String input);
}
