package dev.apolo.api.service;

import dev.apolo.api.model.TransactionModel;
import dev.apolo.api.result.ServiceResult;

import java.util.List;

public interface ITransactionService {
    ServiceResult<TransactionModel> recordTransaction(TransactionModel transaction);
    ServiceResult<List<TransactionModel>> getHistory(String uuid, int limit);
}
