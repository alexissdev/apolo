package dev.apolo.plugin.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import dev.apolo.plugin.config.ApoloConfig;
import dev.apolo.redis.RedisManager;
import dev.apolo.redis.RedisSubscriber;
import dev.apolo.redis.repository.impl.RedisPlayerStateRepository;
import dev.apolo.redis.repository.impl.RedisWarpRepository;

public class RedisModule extends AbstractModule {
    private final ApoloConfig config;

    public RedisModule(ApoloConfig config) {
        this.config = config;
    }

    @Provides
    @Singleton
    public RedisManager provideRedisManager() {
        return new RedisManager(
            config.getRedisHost(), config.getRedisPort(), config.getRedisPassword(),
            config.getRedisDatabase(), config.getRedisMaxTotal(),
            config.getRedisMaxIdle(), config.getRedisMinIdle()
        );
    }

    @Provides
    @Singleton
    public RedisSubscriber provideRedisSubscriber(RedisManager redisManager) {
        return new RedisSubscriber(redisManager);
    }

    @Provides
    @Singleton
    public RedisWarpRepository provideRedisWarpRepository(RedisManager redisManager) {
        return new RedisWarpRepository(redisManager);
    }

    @Provides
    @Singleton
    public RedisPlayerStateRepository provideRedisPlayerStateRepository(RedisManager redisManager) {
        return new RedisPlayerStateRepository(redisManager);
    }
}
