package dev.apolo.api.service;

import dev.apolo.api.model.RepairResultModel;
import dev.apolo.api.result.ServiceResult;
import org.bukkit.entity.Player;

public interface IRepairService {
    ServiceResult<RepairResultModel> repairHand(Player player);
    ServiceResult<RepairResultModel> repairArmor(Player player);
    ServiceResult<RepairResultModel> repairAll(Player player);
    boolean isOnCooldown(Player player);
    long getCooldownRemaining(Player player);
}
