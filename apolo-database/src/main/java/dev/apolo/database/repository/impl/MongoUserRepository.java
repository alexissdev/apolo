package dev.apolo.database.repository.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import dev.apolo.api.model.UserModel;
import dev.apolo.core.repository.interfaces.IUserRepository;
import dev.apolo.database.MongoManager;
import dev.apolo.database.collection.CollectionNames;
import dev.apolo.database.mapper.UserDocumentMapper;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
public class MongoUserRepository implements IUserRepository {
    private final MongoCollection<Document> collection;
    private final UserDocumentMapper mapper;
    private final Executor executor;

    public MongoUserRepository(MongoManager mongoManager) {
        this.collection = mongoManager.getCollection(CollectionNames.USERS);
        this.mapper = new UserDocumentMapper();
        this.executor = Executors.newFixedThreadPool(4);
        createIndexes();
    }

    private void createIndexes() {
        CompletableFuture.runAsync(() -> {
            try {
                collection.createIndex(Indexes.ascending("uuid"), new IndexOptions().unique(true));
                collection.createIndex(Indexes.ascending("username"));
                collection.createIndex(Indexes.descending("balance"));
            } catch (Exception e) {
                log.error("Error creating MongoDB indexes", e);
            }
        }, executor);
    }

    public CompletableFuture<Optional<UserModel>> findByUuid(String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            Document doc = collection.find(Filters.eq("uuid", uuid)).first();
            return Optional.ofNullable(doc).map(mapper::fromDocument);
        }, executor);
    }

    public CompletableFuture<Optional<UserModel>> findByUsername(String username) {
        return CompletableFuture.supplyAsync(() -> {
            Document doc = collection.find(Filters.eq("username", username)).first();
            return Optional.ofNullable(doc).map(mapper::fromDocument);
        }, executor);
    }

    public CompletableFuture<UserModel> save(UserModel user) {
        return CompletableFuture.supplyAsync(() -> {
            Document doc = mapper.toDocument(user);
            collection.replaceOne(
                Filters.eq("uuid", user.getUuid()),
                doc,
                new ReplaceOptions().upsert(true)
            );
            return user;
        }, executor);
    }

    public CompletableFuture<UserModel> update(UserModel user) {
        return save(user);
    }

    public CompletableFuture<Boolean> exists(String uuid) {
        return CompletableFuture.supplyAsync(() ->
            collection.find(Filters.eq("uuid", uuid)).first() != null, executor);
    }

    public CompletableFuture<Void> updateBalance(String uuid, double newBalance) {
        return CompletableFuture.runAsync(() ->
            collection.updateOne(
                Filters.eq("uuid", uuid),
                Updates.set("balance", newBalance)
            ), executor);
    }

    public CompletableFuture<Void> updateLastSeen(String uuid, long timestamp) {
        return CompletableFuture.runAsync(() ->
            collection.updateOne(
                Filters.eq("uuid", uuid),
                Updates.set("lastSeen", timestamp)
            ), executor);
    }

    public CompletableFuture<Void> updateFlyState(String uuid, boolean enabled) {
        return CompletableFuture.runAsync(() ->
            collection.updateOne(
                Filters.eq("uuid", uuid),
                Updates.set("flyEnabled", enabled)
            ), executor);
    }

    public CompletableFuture<Void> updateGodState(String uuid, boolean enabled) {
        return CompletableFuture.runAsync(() ->
            collection.updateOne(
                Filters.eq("uuid", uuid),
                Updates.set("godModeEnabled", enabled)
            ), executor);
    }

    public CompletableFuture<List<UserModel>> findTopBalances(int limit) {
        return CompletableFuture.supplyAsync(() -> {
            List<UserModel> users = new ArrayList<>();
            collection.find()
                .sort(Sorts.descending("balance"))
                .limit(limit)
                .forEach(doc -> users.add(mapper.fromDocument(doc)));
            return users;
        }, executor);
    }
}
