package dev.apolo.database.repository.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import dev.apolo.api.model.TransactionModel;
import dev.apolo.core.repository.interfaces.ITransactionRepository;
import dev.apolo.database.MongoManager;
import dev.apolo.database.collection.CollectionNames;
import dev.apolo.database.mapper.TransactionDocumentMapper;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MongoTransactionRepository implements ITransactionRepository {
    private final MongoCollection<Document> collection;
    private final TransactionDocumentMapper mapper;

    public MongoTransactionRepository(MongoManager mongoManager) {
        this.collection = mongoManager.getCollection(CollectionNames.TRANSACTIONS);
        this.mapper = new TransactionDocumentMapper();
        createIndexes();
    }

    private void createIndexes() {
        try {
            collection.createIndex(Indexes.ascending("fromUuid"));
            collection.createIndex(Indexes.ascending("toUuid"));
            collection.createIndex(Indexes.descending("timestamp"));
        } catch (Exception e) {
            log.error("Error creating transaction indexes", e);
        }
    }

    @Override
    public TransactionModel save(TransactionModel transaction) {
        Document doc = mapper.toDocument(transaction);
        collection.insertOne(doc);
        return transaction;
    }

    @Override
    public List<TransactionModel> findByPlayer(String uuid, int limit) {
        List<TransactionModel> transactions = new ArrayList<>();
        collection.find(
            Filters.or(
                Filters.eq("fromUuid", uuid),
                Filters.eq("toUuid", uuid)
            ))
            .sort(Sorts.descending("timestamp"))
            .limit(limit)
            .forEach(doc -> transactions.add(mapper.fromDocument(doc)));
        return transactions;
    }

    @Override
    public List<TransactionModel> findBetween(String uuid1, String uuid2, int limit) {
        List<TransactionModel> transactions = new ArrayList<>();
        collection.find(
            Filters.or(
                Filters.and(Filters.eq("fromUuid", uuid1), Filters.eq("toUuid", uuid2)),
                Filters.and(Filters.eq("fromUuid", uuid2), Filters.eq("toUuid", uuid1))
            ))
            .sort(Sorts.descending("timestamp"))
            .limit(limit)
            .forEach(doc -> transactions.add(mapper.fromDocument(doc)));
        return transactions;
    }
}
