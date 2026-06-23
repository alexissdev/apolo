package dev.apolo.core.usecase;

import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IFlyService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class ToggleFlyUseCase implements UseCase<ToggleFlyUseCase.Input, Boolean> {
    private final IFlyService flyService;

    @Override
    public ServiceResult<Boolean> execute(Input input) {
        return flyService.toggleFly(input.getTarget());
    }

    @Value
    @Builder
    public static class Input {
        Player target;
    }
}
