package dev.apolo.core.usecase;

import dev.apolo.api.model.RepairResultModel;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IRepairService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class RepairAllUseCase implements UseCase<RepairAllUseCase.Input, RepairResultModel> {
    private final IRepairService repairService;

    @Override
    public ServiceResult<RepairResultModel> execute(Input input) {
        return repairService.repairAll(input.getPlayer());
    }

    @Value
    @Builder
    public static class Input {
        Player player;
    }
}
