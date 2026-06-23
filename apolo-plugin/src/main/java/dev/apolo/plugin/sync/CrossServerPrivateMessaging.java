package dev.apolo.plugin.sync;

import com.google.gson.Gson;
import dev.apolo.api.model.PrivateMessageModel;
import dev.apolo.api.service.IPrivateMessageService;
import dev.apolo.redis.RedisKeys;
import dev.apolo.redis.RedisManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

@Slf4j
@RequiredArgsConstructor
public class CrossServerPrivateMessaging {
    private final RedisManager redisManager;
    private final Gson gson = new Gson();

    private JedisPubSub pubSub;
    private Thread subscriberThread;

    public void publish(PrivateMessageModel message) {
        try {
            redisManager.publish(RedisKeys.CHANNEL_PRIVATE_MESSAGE, gson.toJson(message));
        } catch (Exception e) {
            log.error("Error publishing private message cross-server", e);
        }
    }

    public void startListening(IPrivateMessageService privateMessageService) {
        pubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                try {
                    PrivateMessageModel model = gson.fromJson(message, PrivateMessageModel.class);
                    privateMessageService.deliverLocally(model);
                } catch (Exception e) {
                    log.error("Error processing cross-server private message", e);
                }
            }
        };

        subscriberThread = new Thread(() -> {
            try (Jedis jedis = redisManager.getPool().getResource()) {
                jedis.subscribe(pubSub, RedisKeys.CHANNEL_PRIVATE_MESSAGE);
            } catch (Exception e) {
                log.error("Private message subscriber thread error", e);
            }
        }, "apolo-private-msg-subscriber");
        subscriberThread.setDaemon(true);
        subscriberThread.start();
        log.info("Cross-server private messaging active.");
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
