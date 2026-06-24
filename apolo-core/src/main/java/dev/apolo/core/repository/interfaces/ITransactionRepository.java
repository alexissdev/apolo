package dev.apolo.core.repository.interfaces;

import dev.apolo.api.model.TransactionModel;

import java.util.List;

public interface ITransactionRepository {
    TransactionModel save(TransactionModel transaction);
    List<TransactionModel> findByPlayer(String uuid, int limit);
    List<TransactionModel> findBetween(String uuid1, String uuid2, int limit);
}
