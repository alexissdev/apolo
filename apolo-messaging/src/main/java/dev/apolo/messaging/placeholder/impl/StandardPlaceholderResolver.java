package dev.apolo.messaging.placeholder.impl;

import dev.apolo.messaging.placeholder.PlaceholderResolver;

import java.util.Map;

public class StandardPlaceholderResolver implements PlaceholderResolver {

    @Override
    public String resolve(String message, Map<String, String> placeholders) {
        String result = message;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }
}
