package dev.apolo.core.repair;

import dev.apolo.api.enums.RepairType;
import dev.apolo.api.model.RepairResultModel;
import dev.apolo.api.result.ServiceResult;
import org.bukkit.entity.Player;

public interface RepairStrategy {
    ServiceResult<RepairResultModel> repair(Player player);
    RepairType getType();
}
