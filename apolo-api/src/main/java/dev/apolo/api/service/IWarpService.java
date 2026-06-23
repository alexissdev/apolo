package dev.apolo.api.service;

import dev.apolo.api.model.WarpModel;
import dev.apolo.api.result.ServiceResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public interface IWarpService {
    ServiceResult<Void> createWarp(String name, Location location, Player creator);
    ServiceResult<Void> deleteWarp(String name);
    ServiceResult<WarpModel> getWarp(String name);
    ServiceResult<List<WarpModel>> getAllWarps();
    ServiceResult<Void> teleportToWarp(Player player, String name);
    boolean warpExists(String name);
}
