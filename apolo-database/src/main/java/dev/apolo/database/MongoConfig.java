package dev.apolo.database;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MongoConfig {
    String uri;
    String database;
    int connectionTimeout;
    int socketTimeout;
    int maxPoolSize;
}
