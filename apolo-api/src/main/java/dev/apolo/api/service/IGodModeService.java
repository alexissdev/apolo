package dev.apolo.api.service;

import dev.apolo.api.result.ServiceResult;
import org.bukkit.entity.Player;

public interface IGodModeService {
    ServiceResult<Boolean> toggleGodMode(Player target);
    ServiceResult<Void> enableGodMode(Player target);
    ServiceResult<Void> disableGodMode(Player target);
    boolean hasGodMode(Player player);
    ServiceResult<Void> restoreState(Player player);
}
