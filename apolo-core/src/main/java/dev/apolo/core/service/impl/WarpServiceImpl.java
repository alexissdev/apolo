package dev.apolo.core.service.impl;

import dev.apolo.api.event.ApoloWarpCreateEvent;
import dev.apolo.api.event.ApoloWarpDeleteEvent;
import dev.apolo.api.event.ApoloWarpTeleportEvent;
import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.model.WarpModel;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IWarpService;
import dev.apolo.core.repository.interfaces.IWarpRepository;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class WarpServiceImpl implements IWarpService {
    private final IWarpRepository warpRepository;

    @Override
    public ServiceResult<Void> createWarp(String name, Location location, Player creator) {
        if (warpRepository.exists(name)) {
            return ServiceResult.alreadyExists(MessageKey.WARP_ALREADY_EXISTS,
                Collections.singletonMap("warp", name));
        }

        WarpModel warp = WarpModel.builder()
            .name(name)
            .worldName(location.getWorld().getName())
            .x(location.getX())
            .y(location.getY())
            .z(location.getZ())
            .yaw(location.getYaw())
            .pitch(location.getPitch())
            .createdBy(creator.getName())
            .createdAt(System.currentTimeMillis())
            .build();

        ApoloWarpCreateEvent event = new ApoloWarpCreateEvent(creator, warp);
        creator.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return ServiceResult.failure(MessageKey.NO_PERMISSION);
        }

        warpRepository.save(warp);
        return ServiceResult.success();
    }

    @Override
    public ServiceResult<Void> deleteWarp(String name) {
        if (!warpRepository.exists(name)) {
            return ServiceResult.notFound(MessageKey.WARP_NOT_FOUND,
                Collections.singletonMap("warp", name));
        }

        WarpModel warp = warpRepository.findByName(name).orElse(null);
        if (warp != null) {
            ApoloWarpDeleteEvent event = new ApoloWarpDeleteEvent(name, warp);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return ServiceResult.failure(MessageKey.NO_PERMISSION);
            }
        }

        warpRepository.delete(name);
        return ServiceResult.success();
    }

    @Override
    public ServiceResult<WarpModel> getWarp(String name) {
        return warpRepository.findByName(name)
            .map(ServiceResult::success)
            .orElse(ServiceResult.notFound(MessageKey.WARP_NOT_FOUND,
                Collections.singletonMap("warp", name)));
    }

    @Override
    public ServiceResult<List<WarpModel>> getAllWarps() {
        return ServiceResult.success(warpRepository.findAll());
    }

    @Override
    public ServiceResult<Void> teleportToWarp(Player player, String name) {
        return warpRepository.findByName(name).map(warp -> {
            ApoloWarpTeleportEvent event = new ApoloWarpTeleportEvent(player, warp);
            player.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return ServiceResult.<Void>failure(MessageKey.NO_PERMISSION);
            }
            World world = player.getServer().getWorld(warp.getWorldName());
            if (world == null) {
                return ServiceResult.<Void>failure(MessageKey.WARP_NOT_FOUND,
                    Collections.singletonMap("warp", name));
            }
            Location loc = new Location(world, warp.getX(), warp.getY(), warp.getZ(),
                warp.getYaw(), warp.getPitch());
            player.teleport(loc);
            return ServiceResult.<Void>success();
        }).orElse(ServiceResult.notFound(MessageKey.WARP_NOT_FOUND,
            Collections.singletonMap("warp", name)));
    }

    @Override
    public boolean warpExists(String name) {
        return warpRepository.exists(name);
    }
}
