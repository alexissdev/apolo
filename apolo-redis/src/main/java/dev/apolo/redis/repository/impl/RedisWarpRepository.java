package dev.apolo.redis.repository.impl;

import com.google.gson.Gson;
import dev.apolo.api.model.WarpModel;
import dev.apolo.core.repository.interfaces.IWarpRepository;
import dev.apolo.redis.RedisKeys;
import dev.apolo.redis.RedisManager;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RedisWarpRepository implements IWarpRepository {
    private final RedisManager redisManager;
    private final Gson gson = new Gson();

    public void save(WarpModel warp) {
        String json = gson.toJson(warp);
        redisManager.set(RedisKeys.warp(warp.getName()), json);
        redisManager.sadd(RedisKeys.warpList(), warp.getName().toLowerCase());
    }

    public Optional<WarpModel> findByName(String name) {
        String json = redisManager.get(RedisKeys.warp(name));
        if (json == null) return Optional.empty();
        return Optional.of(gson.fromJson(json, WarpModel.class));
    }

    public Set<String> getAllNames() {
        return redisManager.smembers(RedisKeys.warpList());
    }

    public List<WarpModel> findAll() {
        return getAllNames().stream()
            .map(this::findByName)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    public void delete(String name) {
        redisManager.delete(RedisKeys.warp(name));
        redisManager.srem(RedisKeys.warpList(), name.toLowerCase());
    }

    public boolean exists(String name) {
        return redisManager.exists(RedisKeys.warp(name));
    }
}
