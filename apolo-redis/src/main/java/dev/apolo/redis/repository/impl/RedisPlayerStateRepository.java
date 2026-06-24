package dev.apolo.redis.repository.impl;

import dev.apolo.core.repository.interfaces.IPlayerStateRepository;
import dev.apolo.redis.RedisKeys;
import dev.apolo.redis.RedisManager;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class RedisPlayerStateRepository implements IPlayerStateRepository {
    private final RedisManager redisManager;

    public void setFlyState(String uuid, boolean enabled) {
        redisManager.set(RedisKeys.flyState(uuid), String.valueOf(enabled));
    }

    public boolean getFlyState(String uuid) {
        String value = redisManager.get(RedisKeys.flyState(uuid));
        return Boolean.parseBoolean(value);
    }

    public boolean hasFlyState(String uuid) {
        return redisManager.exists(RedisKeys.flyState(uuid));
    }

    public void deleteFlyState(String uuid) {
        redisManager.delete(RedisKeys.flyState(uuid));
    }

    public void setGodState(String uuid, boolean enabled) {
        redisManager.set(RedisKeys.godState(uuid), String.valueOf(enabled));
    }

    public boolean getGodState(String uuid) {
        String value = redisManager.get(RedisKeys.godState(uuid));
        return Boolean.parseBoolean(value);
    }

    public boolean hasGodState(String uuid) {
        return redisManager.exists(RedisKeys.godState(uuid));
    }

    public void deleteGodState(String uuid) {
        redisManager.delete(RedisKeys.godState(uuid));
    }

    public void setWarpCooldown(String uuid, int ttlSeconds) {
        redisManager.setEx(RedisKeys.warpCooldown(uuid), ttlSeconds, "1");
    }

    public boolean hasWarpCooldown(String uuid) {
        return redisManager.exists(RedisKeys.warpCooldown(uuid));
    }

    public long getWarpCooldownTtl(String uuid) {
        return redisManager.ttl(RedisKeys.warpCooldown(uuid));
    }

    public void setRepairCooldown(String uuid, int ttlSeconds) {
        redisManager.setEx(RedisKeys.repairCooldown(uuid), ttlSeconds, "1");
    }

    public boolean hasRepairCooldown(String uuid) {
        return redisManager.exists(RedisKeys.repairCooldown(uuid));
    }

    public long getRepairCooldownTtl(String uuid) {
        return redisManager.ttl(RedisKeys.repairCooldown(uuid));
    }

    public void setTpaSent(String senderUuid, String targetUuid, int ttlSeconds) {
        redisManager.setEx(RedisKeys.tpaSent(senderUuid), ttlSeconds, targetUuid);
    }

    public String getTpaSent(String senderUuid) {
        return redisManager.get(RedisKeys.tpaSent(senderUuid));
    }

    public boolean hasTpaSent(String senderUuid) {
        return redisManager.exists(RedisKeys.tpaSent(senderUuid));
    }

    public void deleteTpaSent(String senderUuid) {
        redisManager.delete(RedisKeys.tpaSent(senderUuid));
    }

    public void setTpaCooldown(String uuid, int ttlSeconds) {
        redisManager.setEx(RedisKeys.tpaCooldown(uuid), ttlSeconds, "1");
    }

    public boolean hasTpaCooldown(String uuid) {
        return redisManager.exists(RedisKeys.tpaCooldown(uuid));
    }

    public long getTpaCooldownTtl(String uuid) {
        return redisManager.ttl(RedisKeys.tpaCooldown(uuid));
    }

    public void setTpaRequest(String targetUuid, String json, int ttlSeconds) {
        redisManager.setEx(RedisKeys.tpaPending(targetUuid), ttlSeconds, json);
    }

    public String getTpaRequest(String targetUuid) {
        return redisManager.get(RedisKeys.tpaPending(targetUuid));
    }

    public boolean hasTpaRequest(String targetUuid) {
        return redisManager.exists(RedisKeys.tpaPending(targetUuid));
    }

    public void deleteTpaRequest(String targetUuid) {
        redisManager.delete(RedisKeys.tpaPending(targetUuid));
    }

    public void setCachedBalance(String uuid, double balance, int ttlSeconds) {
        redisManager.setEx(RedisKeys.economyBalance(uuid), ttlSeconds, String.valueOf(balance));
    }

    public Optional<Double> getCachedBalance(String uuid) {
        String value = redisManager.get(RedisKeys.economyBalance(uuid));
        if (value == null) return Optional.empty();
        try {
            return Optional.of(Double.parseDouble(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public void deleteCachedBalance(String uuid) {
        redisManager.delete(RedisKeys.economyBalance(uuid));
    }

    @Override
    public void registerOnlinePlayer(String uuid, String name) {
        redisManager.sadd(RedisKeys.ONLINE_PLAYERS_SET, uuid);
        redisManager.sadd(RedisKeys.ONLINE_NAMES_SET, name.toLowerCase());
        redisManager.set(RedisKeys.onlinePlayerName(uuid), name);
        redisManager.set(RedisKeys.onlinePlayerUuid(name), uuid);
    }

    @Override
    public void unregisterOnlinePlayer(String uuid, String name) {
        redisManager.srem(RedisKeys.ONLINE_PLAYERS_SET, uuid);
        redisManager.srem(RedisKeys.ONLINE_NAMES_SET, name.toLowerCase());
        redisManager.delete(RedisKeys.onlinePlayerName(uuid));
        redisManager.delete(RedisKeys.onlinePlayerUuid(name));
    }

    @Override
    public Set<String> getOnlinePlayerNames() {
        return redisManager.smembers(RedisKeys.ONLINE_NAMES_SET);
    }

    @Override
    public Optional<String> getPlayerUuidByName(String name) {
        return Optional.ofNullable(redisManager.get(RedisKeys.onlinePlayerUuid(name)));
    }

    @Override
    public Optional<String> getPlayerNameByUuid(String uuid) {
        return Optional.ofNullable(redisManager.get(RedisKeys.onlinePlayerName(uuid)));
    }

    @Override
    public void setLastMessageSender(String targetUuid, String senderUuid) {
        redisManager.set(RedisKeys.msgLastSender(targetUuid), senderUuid);
    }

    @Override
    public Optional<String> getLastMessageSender(String uuid) {
        return Optional.ofNullable(redisManager.get(RedisKeys.msgLastSender(uuid)));
    }

    @Override
    public void setSocialSpy(String uuid, boolean enabled) {
        if (enabled) {
            redisManager.sadd(RedisKeys.SOCIALSPY_PLAYERS_SET, uuid);
        } else {
            redisManager.srem(RedisKeys.SOCIALSPY_PLAYERS_SET, uuid);
        }
    }

    @Override
    public boolean isSocialSpy(String uuid) {
        return redisManager.sismember(RedisKeys.SOCIALSPY_PLAYERS_SET, uuid);
    }

    @Override
    public Set<String> getSocialSpyPlayers() {
        return redisManager.smembers(RedisKeys.SOCIALSPY_PLAYERS_SET);
    }

    @Override
    public void setCommandSpy(String uuid, boolean enabled) {
        if (enabled) {
            redisManager.sadd(RedisKeys.COMMANDSPY_PLAYERS_SET, uuid);
        } else {
            redisManager.srem(RedisKeys.COMMANDSPY_PLAYERS_SET, uuid);
        }
    }

    @Override
    public boolean isCommandSpy(String uuid) {
        return redisManager.sismember(RedisKeys.COMMANDSPY_PLAYERS_SET, uuid);
    }

    @Override
    public Set<String> getCommandSpyPlayers() {
        return redisManager.smembers(RedisKeys.COMMANDSPY_PLAYERS_SET);
    }
}
