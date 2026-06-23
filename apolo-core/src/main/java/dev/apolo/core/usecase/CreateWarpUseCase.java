package dev.apolo.core.usecase;

import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IWarpService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class CreateWarpUseCase implements UseCase<CreateWarpUseCase.Input, Void> {
    private final IWarpService warpService;

    @Override
    public ServiceResult<Void> execute(Input input) {
        return warpService.createWarp(input.getName(), input.getLocation(), input.getCreator());
    }

    @Value
    @Builder
    public static class Input {
        String name;
        Location location;
        Player creator;
    }
}
