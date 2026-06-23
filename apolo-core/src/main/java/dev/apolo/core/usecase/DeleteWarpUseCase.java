package dev.apolo.core.usecase;

import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IWarpService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
public class DeleteWarpUseCase implements UseCase<DeleteWarpUseCase.Input, Void> {
    private final IWarpService warpService;

    @Override
    public ServiceResult<Void> execute(Input input) {
        return warpService.deleteWarp(input.getName());
    }

    @Value
    @Builder
    public static class Input {
        String name;
    }
}
