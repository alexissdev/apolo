package dev.apolo.api.service;

import dev.apolo.api.result.ServiceResult;
import org.bukkit.entity.Player;

public interface IFlyService {
    ServiceResult<Boolean> toggleFly(Player target);
    ServiceResult<Void> enableFly(Player target);
    ServiceResult<Void> disableFly(Player target);
    boolean hasFly(Player player);
    ServiceResult<Void> restoreState(Player player);
}
