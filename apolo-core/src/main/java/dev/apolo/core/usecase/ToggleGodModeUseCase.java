package dev.apolo.core.usecase;

import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IGodModeService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class ToggleGodModeUseCase implements UseCase<ToggleGodModeUseCase.Input, Boolean> {
    private final IGodModeService godModeService;

    @Override
    public ServiceResult<Boolean> execute(Input input) {
        return godModeService.toggleGodMode(input.getTarget());
    }

    @Value
    @Builder
    public static class Input {
        Player target;
    }
}
