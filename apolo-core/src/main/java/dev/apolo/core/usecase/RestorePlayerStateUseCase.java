package dev.apolo.core.usecase;

import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IFlyService;
import dev.apolo.api.service.IGodModeService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class RestorePlayerStateUseCase implements UseCase<RestorePlayerStateUseCase.Input, Void> {
    private final IFlyService flyService;
    private final IGodModeService godModeService;

    @Override
    public ServiceResult<Void> execute(Input input) {
        flyService.restoreState(input.getPlayer());
        godModeService.restoreState(input.getPlayer());
        return ServiceResult.success();
    }

    @Value
    @Builder
    public static class Input {
        Player player;
    }
}
