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

@Slf4j
public class MongoUserRepository implements IUserRepository {
    private final MongoCollection<Document> collection;
    private final UserDocumentMapper mapper;

    public MongoUserRepository(MongoManager mongoManager) {
        this.collection = mongoManager.getCollection(CollectionNames.USERS);
        this.mapper = new UserDocumentMapper();
        createIndexes();
    }

    private void createIndexes() {
        try {
            collection.createIndex(Indexes.ascending("uuid"), new IndexOptions().unique(true));
            collection.createIndex(Indexes.ascending("username"));
            collection.createIndex(Indexes.descending("balance"));
        } catch (Exception e) {
            log.error("Error creating MongoDB user indexes", e);
        }
    }

    @Override
    public Optional<UserModel> findByUuid(String uuid) {
        Document doc = collection.find(Filters.eq("uuid", uuid)).first();
        return Optional.ofNullable(doc).map(mapper::fromDocument);
    }

    @Override
    public Optional<UserModel> findByUsername(String username) {
        Document doc = collection.find(Filters.eq("username", username)).first();
        return Optional.ofNullable(doc).map(mapper::fromDocument);
    }

    @Override
    public UserModel save(UserModel user) {
        Document doc = mapper.toDocument(user);
        collection.replaceOne(
            Filters.eq("uuid", user.getUuid()),
            doc,
            new ReplaceOptions().upsert(true)
        );
        return user;
    }

    @Override
    public UserModel update(UserModel user) {
        return save(user);
    }

    @Override
    public boolean exists(String uuid) {
        return collection.find(Filters.eq("uuid", uuid)).first() != null;
    }

    @Override
    public void updateBalance(String uuid, double newBalance) {
        collection.updateOne(
            Filters.eq("uuid", uuid),
            Updates.set("balance", newBalance)
        );
    }

    @Override
    public void updateLastSeen(String uuid, long timestamp) {
        collection.updateOne(
            Filters.eq("uuid", uuid),
            Updates.set("lastSeen", timestamp)
        );
    }

    @Override
    public void updateFlyState(String uuid, boolean enabled) {
        collection.updateOne(
            Filters.eq("uuid", uuid),
            Updates.set("flyEnabled", enabled)
        );
    }

    @Override
    public void updateGodState(String uuid, boolean enabled) {
        collection.updateOne(
            Filters.eq("uuid", uuid),
            Updates.set("godModeEnabled", enabled)
        );
    }

    @Override
    public List<UserModel> findTopBalances(int limit) {
        List<UserModel> users = new ArrayList<>();
        collection.find()
            .sort(Sorts.descending("balance"))
            .limit(limit)
            .forEach(doc -> users.add(mapper.fromDocument(doc)));
        return users;
    }
}
