package dev.apolo.redis;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.function.BiConsumer;

@Slf4j
public class RedisSubscriber {
    private final RedisManager redisManager;
    private JedisPubSub pubSub;
    private Thread subscribeThread;

    public RedisSubscriber(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

    public void subscribe(String[] channels, BiConsumer<String, String> messageHandler) {
        pubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                messageHandler.accept(channel, message);
            }
        };

        subscribeThread = new Thread(() -> {
            try (Jedis jedis = redisManager.getPool().getResource()) {
                jedis.subscribe(pubSub, channels);
            } catch (Exception e) {
                log.error("Redis subscriber error", e);
            }
        }, "apolo-redis-subscriber");
        subscribeThread.setDaemon(true);
        subscribeThread.start();
    }

    public void unsubscribe() {
        if (pubSub != null && pubSub.isSubscribed()) {
            pubSub.unsubscribe();
        }
        if (subscribeThread != null) {
            subscribeThread.interrupt();
        }
    }
}
