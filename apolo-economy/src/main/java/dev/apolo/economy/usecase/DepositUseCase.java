package dev.apolo.economy.usecase;

import dev.apolo.api.enums.TransactionStatus;
import dev.apolo.api.enums.TransactionType;
import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.model.TransactionModel;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.core.repository.interfaces.IPlayerStateRepository;
import dev.apolo.core.repository.interfaces.ITransactionRepository;
import dev.apolo.core.repository.interfaces.IUserRepository;
import dev.apolo.core.usecase.UseCase;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.UUID;

@RequiredArgsConstructor
public class DepositUseCase implements UseCase<DepositUseCase.Input, Void> {
    private final IUserRepository userRepository;
    private final ITransactionRepository transactionRepository;
    private final IPlayerStateRepository playerStateRepository;
    private final double maxBalance;
    private final int balanceCacheTtl;

    @Override
    public ServiceResult<Void> execute(Input input) {
        if (input.getAmount() <= 0) {
            return ServiceResult.failure(MessageKey.ECONOMY_INVALID_AMOUNT);
        }

        try {
            userRepository.findByUuid(input.getUuid()).thenAccept(optUser -> {
                optUser.ifPresent(user -> {
                    double newBalance = Math.min(user.getBalance() + input.getAmount(), maxBalance);
                    userRepository.updateBalance(input.getUuid(), newBalance);
                    playerStateRepository.setCachedBalance(input.getUuid(), newBalance, balanceCacheTtl);

                    TransactionModel transaction = TransactionModel.builder()
                        .id(UUID.randomUUID().toString())
                        .fromUuid(null)
                        .toUuid(input.getUuid())
                        .amount(input.getAmount())
                        .type(TransactionType.DEPOSIT)
                        .reason(input.getReason())
                        .timestamp(System.currentTimeMillis())
                        .status(TransactionStatus.SUCCESS)
                        .build();
                    transactionRepository.save(transaction);
                });
            }).get();
            return ServiceResult.success();
        } catch (Exception e) {
            return ServiceResult.failure(MessageKey.ECONOMY_ACCOUNT_NOT_FOUND);
        }
    }

    @Value
    @Builder
    public static class Input {
        String uuid;
        double amount;
        String reason;
    }
}
