package dev.apolo.core.usecase;

import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IGameModeService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class SetGameModeUseCase implements UseCase<SetGameModeUseCase.Input, Void> {
    private final IGameModeService gameModeService;

    @Override
    public ServiceResult<Void> execute(Input input) {
        return gameModeService.setGameMode(input.getTarget(), input.getMode());
    }

    @Value
    @Builder
    public static class Input {
        Player target;
        GameMode mode;
    }
}
