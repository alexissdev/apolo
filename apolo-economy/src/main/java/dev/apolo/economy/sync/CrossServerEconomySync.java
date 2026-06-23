package dev.apolo.economy.sync;

import com.google.gson.Gson;
import dev.apolo.core.repository.interfaces.IPlayerStateRepository;
import dev.apolo.redis.RedisKeys;
import dev.apolo.redis.RedisManager;
import dev.apolo.redis.RedisSubscriber;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CrossServerEconomySync {
    private final RedisManager redisManager;
    private final IPlayerStateRepository playerStateRepository;
    private final RedisSubscriber redisSubscriber;
    private final int balanceCacheTtl;
    private final Gson gson = new Gson();

    public CrossServerEconomySync(RedisManager redisManager,
                                   IPlayerStateRepository playerStateRepository,
                                   RedisSubscriber redisSubscriber,
                                   int balanceCacheTtl) {
        this.redisManager = redisManager;
        this.playerStateRepository = playerStateRepository;
        this.redisSubscriber = redisSubscriber;
        this.balanceCacheTtl = balanceCacheTtl;
    }

    public void startListening() {
        redisSubscriber.subscribe(
            new String[]{RedisKeys.CHANNEL_ECONOMY_UPDATE},
            (channel, message) -> {
                if (RedisKeys.CHANNEL_ECONOMY_UPDATE.equals(channel)) {
                    handleEconomyUpdate(message);
                }
            }
        );
        log.info("Cross-server economy sync active.");
    }

    public void publishBalanceUpdate(String uuid, double newBalance) {
        try {
            String json = gson.toJson(new EconomyUpdateMessage(uuid, newBalance));
            redisManager.publish(RedisKeys.CHANNEL_ECONOMY_UPDATE, json);
        } catch (Exception e) {
            log.error("Error publishing economy update for uuid {}", uuid, e);
        }
    }

    private void handleEconomyUpdate(String json) {
        try {
            EconomyUpdateMessage message = gson.fromJson(json, EconomyUpdateMessage.class);
            playerStateRepository.setCachedBalance(message.getUuid(), message.getNewBalance(), balanceCacheTtl);
            log.debug("Economy sync: {} -> {}", message.getUuid(), message.getNewBalance());
        } catch (Exception e) {
            log.error("Error processing economy update message", e);
        }
    }

    public void stop() {
        redisSubscriber.unsubscribe();
    }
}
