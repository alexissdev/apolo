package dev.apolo.economy.service.impl;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.model.TransactionModel;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.ITransactionService;
import dev.apolo.core.repository.interfaces.ITransactionRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class TransactionServiceImpl implements ITransactionService {
    private final ITransactionRepository transactionRepository;

    @Override
    public ServiceResult<TransactionModel> recordTransaction(TransactionModel transaction) {
        try {
            TransactionModel saved = transactionRepository.save(transaction);
            return ServiceResult.success(saved);
        } catch (Exception e) {
            return ServiceResult.failure(MessageKey.ECONOMY_ACCOUNT_NOT_FOUND);
        }
    }

    @Override
    public ServiceResult<List<TransactionModel>> getHistory(String uuid, int limit) {
        try {
            List<TransactionModel> history = transactionRepository.findByPlayer(uuid, limit);
            return ServiceResult.success(history);
        } catch (Exception e) {
            return ServiceResult.failure(MessageKey.ECONOMY_ACCOUNT_NOT_FOUND);
        }
    }
}
