package dev.apolo.core.usecase;

import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.ITpaService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class SendTpaRequestUseCase implements UseCase<SendTpaRequestUseCase.Input, Void> {
    private final ITpaService tpaService;

    @Override
    public ServiceResult<Void> execute(Input input) {
        return tpaService.sendRequest(input.getFrom(), input.getTarget());
    }

    @Value
    @Builder
    public static class Input {
        Player from;
        Player target;
    }
}
