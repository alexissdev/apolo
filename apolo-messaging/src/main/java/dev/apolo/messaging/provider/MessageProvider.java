package dev.apolo.messaging.provider;

import dev.apolo.api.messaging.MessageKey;

public interface MessageProvider {
    String getRaw(MessageKey key);
    String getRawByPath(String path);
    void reload();
}
