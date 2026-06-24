package dev.apolo.plugin.sync;

import com.google.gson.Gson;
import dev.apolo.redis.RedisManager;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

@Slf4j
public abstract class AbstractRedisSubscriber {
    protected final RedisManager redisManager;
    protected final Gson gson = new Gson();

    private JedisPubSub pubSub;
    private Thread subscriberThread;

    protected AbstractRedisSubscriber(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

    protected abstract String getChannel();

    protected abstract String getThreadName();

    protected abstract void handleMessage(String message);

    protected void startListening() {
        pubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                try {
                    handleMessage(message);
                } catch (Exception e) {
                    log.error("Error processing message on channel {}", channel, e);
                }
            }
        };

        subscriberThread = new Thread(() -> {
            try (Jedis jedis = redisManager.getPool().getResource()) {
                jedis.subscribe(pubSub, getChannel());
            } catch (Exception e) {
                log.error("Subscriber thread error on channel {}", getChannel(), e);
            }
        }, getThreadName());
        subscriberThread.setDaemon(true);
        subscriberThread.start();
    }

    public void stop() {
        if (pubSub != null && pubSub.isSubscribed()) {
            pubSub.unsubscribe();
        }
        if (subscriberThread != null) {
            subscriberThread.interrupt();
        }
    }
}
