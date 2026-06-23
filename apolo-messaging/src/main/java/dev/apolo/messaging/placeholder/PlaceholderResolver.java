package dev.apolo.messaging.placeholder;

import java.util.Map;

public interface PlaceholderResolver {
    String resolve(String message, Map<String, String> placeholders);
}
