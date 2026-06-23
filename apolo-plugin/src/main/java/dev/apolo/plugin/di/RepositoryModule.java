package dev.apolo.plugin.di;

import com.google.inject.AbstractModule;
import dev.apolo.core.repository.interfaces.IPlayerStateRepository;
import dev.apolo.core.repository.interfaces.ITransactionRepository;
import dev.apolo.core.repository.interfaces.IUserRepository;
import dev.apolo.core.repository.interfaces.IWarpRepository;
import dev.apolo.database.repository.impl.MongoTransactionRepository;
import dev.apolo.database.repository.impl.MongoUserRepository;
import dev.apolo.redis.repository.impl.RedisPlayerStateRepository;
import dev.apolo.redis.repository.impl.RedisWarpRepository;

public class RepositoryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(IWarpRepository.class).to(RedisWarpRepository.class);
        bind(IPlayerStateRepository.class).to(RedisPlayerStateRepository.class);
        bind(IUserRepository.class).to(MongoUserRepository.class);
        bind(ITransactionRepository.class).to(MongoTransactionRepository.class);
    }
}
