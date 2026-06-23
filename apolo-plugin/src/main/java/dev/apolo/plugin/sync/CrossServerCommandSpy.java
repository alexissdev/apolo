package dev.apolo.plugin.sync;

import com.google.gson.Gson;
import dev.apolo.api.model.CommandSpyModel;
import dev.apolo.redis.RedisKeys;
import dev.apolo.redis.RedisManager;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.function.Consumer;

@Slf4j
public class CrossServerCommandSpy {
    private final RedisManager redisManager;
    private final String localServerId;
    private final Gson gson = new Gson();

    private JedisPubSub pubSub;
    private Thread subscriberThread;

    public CrossServerCommandSpy(RedisManager redisManager, String localServerId) {
        this.redisManager = redisManager;
        this.localServerId = localServerId;
    }

    public void publish(CommandSpyModel model) {
        try {
            redisManager.publish(RedisKeys.CHANNEL_COMMAND_SPY, gson.toJson(model));
        } catch (Exception e) {
            log.error("Error publishing command spy event cross-server", e);
        }
    }

    public void startListening(Consumer<CommandSpyModel> handler) {
        pubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                try {
                    CommandSpyModel model = gson.fromJson(message, CommandSpyModel.class);
                    // Skip if originated on this server — already delivered locally by the listener
                    if (localServerId.equals(model.getServerId())) return;
                    handler.accept(model);
                } catch (Exception e) {
                    log.error("Error processing cross-server command spy event", e);
                }
            }
        };

        subscriberThread = new Thread(() -> {
            try (Jedis jedis = redisManager.getPool().getResource()) {
                jedis.subscribe(pubSub, RedisKeys.CHANNEL_COMMAND_SPY);
            } catch (Exception e) {
                log.error("Command spy subscriber thread error", e);
            }
        }, "apolo-commandspy-subscriber");
        subscriberThread.setDaemon(true);
        subscriberThread.start();
        log.info("Cross-server command spy active.");
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
