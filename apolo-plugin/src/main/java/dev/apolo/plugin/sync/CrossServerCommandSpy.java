package dev.apolo.plugin.sync;

import dev.apolo.api.model.CommandSpyModel;
import dev.apolo.redis.RedisKeys;
import dev.apolo.redis.RedisManager;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public class CrossServerCommandSpy extends AbstractRedisSubscriber {
    private final String localServerId;
    private Consumer<CommandSpyModel> handler;

    public CrossServerCommandSpy(RedisManager redisManager, String localServerId) {
        super(redisManager);
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
        this.handler = handler;
        startListening();
        log.info("Cross-server command spy active.");
    }

    @Override
    protected String getChannel() {
        return RedisKeys.CHANNEL_COMMAND_SPY;
    }

    @Override
    protected String getThreadName() {
        return "apolo-commandspy-subscriber";
    }

    @Override
    protected void handleMessage(String message) {
        CommandSpyModel model = gson.fromJson(message, CommandSpyModel.class);
        // Skip if originated on this server — already delivered locally by the listener
        if (localServerId.equals(model.getServerId())) return;
        handler.accept(model);
    }
}
