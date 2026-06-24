package dev.apolo.plugin.sync;

import dev.apolo.api.model.PrivateMessageModel;
import dev.apolo.api.service.IPrivateMessageService;
import dev.apolo.redis.RedisKeys;
import dev.apolo.redis.RedisManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CrossServerPrivateMessaging extends AbstractRedisSubscriber {
    private IPrivateMessageService privateMessageService;

    public CrossServerPrivateMessaging(RedisManager redisManager) {
        super(redisManager);
    }

    public void publish(PrivateMessageModel message) {
        try {
            redisManager.publish(RedisKeys.CHANNEL_PRIVATE_MESSAGE, gson.toJson(message));
        } catch (Exception e) {
            log.error("Error publishing private message cross-server", e);
        }
    }

    public void startListening(IPrivateMessageService privateMessageService) {
        this.privateMessageService = privateMessageService;
        startListening();
        log.info("Cross-server private messaging active.");
    }

    @Override
    protected String getChannel() {
        return RedisKeys.CHANNEL_PRIVATE_MESSAGE;
    }

    @Override
    protected String getThreadName() {
        return "apolo-private-msg-subscriber";
    }

    @Override
    protected void handleMessage(String message) {
        PrivateMessageModel model = gson.fromJson(message, PrivateMessageModel.class);
        privateMessageService.deliverLocally(model);
    }
}
