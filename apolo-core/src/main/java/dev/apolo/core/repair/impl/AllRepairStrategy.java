package dev.apolo.core.repair.impl;

import dev.apolo.api.enums.RepairType;
import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.model.RepairResultModel;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.core.repair.RepairStrategy;
import org.bukkit.entity.Player;

public class AllRepairStrategy implements RepairStrategy {
    private final HandRepairStrategy handStrategy = new HandRepairStrategy();
    private final ArmorRepairStrategy armorStrategy = new ArmorRepairStrategy();

    @Override
    public ServiceResult<RepairResultModel> repair(Player player) {
        ServiceResult<RepairResultModel> handResult = handStrategy.repair(player);
        ServiceResult<RepairResultModel> armorResult = armorStrategy.repair(player);

        int totalRepaired = 0;
        if (handResult.isSuccess()) {
            totalRepaired += handResult.getData().map(RepairResultModel::getItemsRepaired).orElse(0);
        }
        if (armorResult.isSuccess()) {
            totalRepaired += armorResult.getData().map(RepairResultModel::getItemsRepaired).orElse(0);
        }

        if (totalRepaired == 0) {
            return ServiceResult.failure(MessageKey.REPAIR_NOTHING_TO_REPAIR);
        }
        return ServiceResult.success(RepairResultModel.builder()
            .itemsRepaired(totalRepaired)
            .type(RepairType.ALL)
            .build());
    }

    @Override
    public RepairType getType() {
        return RepairType.ALL;
    }
}
