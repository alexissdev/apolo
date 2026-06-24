package dev.apolo.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.apolo.database.codec.ApoloCodecRegistry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Slf4j
public class MongoManager {
    private final MongoClient mongoClient;
    @Getter
    private final MongoDatabase database;

    static {
        Logger.getLogger("dev.apolo.shaded.mongodb").setLevel(Level.WARNING);
    }

    public MongoManager(MongoConfig config) {
        CodecRegistry apoloRegistry = CodecRegistries.fromProviders(new ApoloCodecRegistry());
        CodecRegistry defaultRegistry = MongoClientSettings.getDefaultCodecRegistry();
        CodecRegistry combinedRegistry = CodecRegistries.fromRegistries(apoloRegistry, defaultRegistry);

        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(config.getUri()))
            .codecRegistry(combinedRegistry)
            .applyToConnectionPoolSettings(builder ->
                builder.maxSize(config.getMaxPoolSize()))
            .applyToSocketSettings(builder ->
                builder.connectTimeout(config.getConnectionTimeout(), TimeUnit.MILLISECONDS)
                       .readTimeout(config.getSocketTimeout(), TimeUnit.MILLISECONDS))
            .build();

        this.mongoClient = MongoClients.create(settings);
        this.database = mongoClient.getDatabase(config.getDatabase());
        log.info("MongoDB connected to database: {}", config.getDatabase());
    }

    public <T> MongoCollection<T> getCollection(String name, Class<T> clazz) {
        return database.getCollection(name, clazz);
    }

    public MongoCollection<Document> getCollection(String name) {
        return database.getCollection(name);
    }

    public void shutdown() {
        if (mongoClient != null) {
            mongoClient.close();
            log.info("MongoDB connection closed.");
        }
    }
}
