package dev.apolo.core.service.impl;

import dev.apolo.api.event.ApoloRepairEvent;
import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.model.RepairResultModel;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IRepairService;
import dev.apolo.core.repair.RepairStrategy;
import dev.apolo.core.repair.RepairStrategyFactory;
import dev.apolo.core.repository.interfaces.IPlayerStateRepository;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Collections;

@RequiredArgsConstructor
public class RepairServiceImpl implements IRepairService {
    private final IPlayerStateRepository playerStateRepository;
    private final RepairStrategyFactory strategyFactory;
    private final int repairCooldown;

    @Override
    public ServiceResult<RepairResultModel> repairHand(Player player) {
        return doRepair(player, strategyFactory.getStrategy("hand"));
    }

    @Override
    public ServiceResult<RepairResultModel> repairArmor(Player player) {
        return doRepair(player, strategyFactory.getStrategy("armor"));
    }

    @Override
    public ServiceResult<RepairResultModel> repairAll(Player player) {
        return doRepair(player, strategyFactory.getStrategy("all"));
    }

    private ServiceResult<RepairResultModel> doRepair(Player player, RepairStrategy strategy) {
        String uuid = player.getUniqueId().toString();
        if (!player.hasPermission("apolo.repair.bypass-cooldown") && playerStateRepository.hasRepairCooldown(uuid)) {
            long remaining = playerStateRepository.getRepairCooldownTtl(uuid);
            return ServiceResult.cooldown(MessageKey.REPAIR_COOLDOWN,
                Collections.singletonMap("seconds", String.valueOf(remaining)));
        }

        ApoloRepairEvent event = new ApoloRepairEvent(player, RepairResultModel.builder()
            .itemsRepaired(0).type(strategy.getType()).build());
        player.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return ServiceResult.failure(MessageKey.NO_PERMISSION);
        }

        ServiceResult<RepairResultModel> result = strategy.repair(player);
        if (result.isSuccess()) {
            playerStateRepository.setRepairCooldown(uuid, repairCooldown);
        }
        return result;
    }

    @Override
    public boolean isOnCooldown(Player player) {
        return playerStateRepository.hasRepairCooldown(player.getUniqueId().toString());
    }

    @Override
    public long getCooldownRemaining(Player player) {
        return playerStateRepository.getRepairCooldownTtl(player.getUniqueId().toString());
    }
}
