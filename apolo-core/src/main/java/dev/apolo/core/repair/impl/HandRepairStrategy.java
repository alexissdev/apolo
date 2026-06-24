package dev.apolo.core.repair.impl;

import dev.apolo.api.enums.RepairType;
import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.model.RepairResultModel;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.core.repair.RepairStrategy;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class HandRepairStrategy implements RepairStrategy {

    @Override
    public ServiceResult<RepairResultModel> repair(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == org.bukkit.Material.AIR) {
            return ServiceResult.failure(MessageKey.REPAIR_NOTHING_TO_REPAIR);
        }
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable)) {
            return ServiceResult.failure(MessageKey.REPAIR_NOTHING_TO_REPAIR);
        }
        Damageable damageable = (Damageable) meta;
        if (damageable.getDamage() == 0) {
            return ServiceResult.failure(MessageKey.REPAIR_NOTHING_TO_REPAIR);
        }
        damageable.setDamage(0);
        item.setItemMeta((ItemMeta) damageable);
        return ServiceResult.success(RepairResultModel.builder()
            .itemsRepaired(1)
            .type(RepairType.HAND)
            .build());
    }

    @Override
    public RepairType getType() {
        return RepairType.HAND;
    }
}
