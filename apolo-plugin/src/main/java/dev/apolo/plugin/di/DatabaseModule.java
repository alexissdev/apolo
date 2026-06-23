package dev.apolo.plugin.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import dev.apolo.database.MongoConfig;
import dev.apolo.database.MongoManager;
import dev.apolo.database.repository.impl.MongoTransactionRepository;
import dev.apolo.database.repository.impl.MongoUserRepository;
import dev.apolo.plugin.config.ApoloConfig;

public class DatabaseModule extends AbstractModule {
    private final ApoloConfig config;

    public DatabaseModule(ApoloConfig config) {
        this.config = config;
    }

    @Provides
    @Singleton
    public MongoManager provideMongoManager() {
        MongoConfig mongoConfig = MongoConfig.builder()
            .uri(config.getMongoUri())
            .database(config.getMongoDatabase())
            .connectionTimeout(config.getMongoConnectionTimeout())
            .socketTimeout(config.getMongoSocketTimeout())
            .maxPoolSize(config.getMongoMaxPoolSize())
            .build();
        return new MongoManager(mongoConfig);
    }

    @Provides
    @Singleton
    public MongoUserRepository provideMongoUserRepository(MongoManager mongoManager) {
        return new MongoUserRepository(mongoManager);
    }

    @Provides
    @Singleton
    public MongoTransactionRepository provideMongoTransactionRepository(MongoManager mongoManager) {
        return new MongoTransactionRepository(mongoManager);
    }
}
