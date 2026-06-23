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

public class ArmorRepairStrategy implements RepairStrategy {

    @Override
    public ServiceResult<RepairResultModel> repair(Player player) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        int repaired = 0;
        for (ItemStack piece : armor) {
            if (piece == null || piece.getType().isAir()) continue;
            ItemMeta meta = piece.getItemMeta();
            if (!(meta instanceof Damageable)) continue;
            Damageable damageable = (Damageable) meta;
            if (damageable.getDamage() > 0) {
                damageable.setDamage(0);
                piece.setItemMeta((ItemMeta) damageable);
                repaired++;
            }
        }
        if (repaired == 0) {
            return ServiceResult.failure(MessageKey.REPAIR_NOTHING_TO_REPAIR);
        }
        return ServiceResult.success(RepairResultModel.builder()
            .itemsRepaired(repaired)
            .type(RepairType.ARMOR)
            .build());
    }

    @Override
    public RepairType getType() {
        return RepairType.ARMOR;
    }
}
