package dev.apolo.core.repository.interfaces;

import dev.apolo.api.model.TransactionModel;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ITransactionRepository {
    CompletableFuture<TransactionModel> save(TransactionModel transaction);
    CompletableFuture<List<TransactionModel>> findByPlayer(String uuid, int limit);
    CompletableFuture<List<TransactionModel>> findBetween(String uuid1, String uuid2, int limit);
}
