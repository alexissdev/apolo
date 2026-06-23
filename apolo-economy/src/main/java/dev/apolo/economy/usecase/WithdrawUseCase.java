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
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public class WithdrawUseCase implements UseCase<WithdrawUseCase.Input, Void> {
    private final IUserRepository userRepository;
    private final ITransactionRepository transactionRepository;
    private final IPlayerStateRepository playerStateRepository;
    private final int balanceCacheTtl;

    @Override
    public ServiceResult<Void> execute(Input input) {
        if (input.getAmount() <= 0) {
            return ServiceResult.failure(MessageKey.ECONOMY_INVALID_AMOUNT);
        }

        AtomicBoolean insufficient = new AtomicBoolean(false);
        try {
            userRepository.findByUuid(input.getUuid()).thenAccept(optUser -> {
                optUser.ifPresent(user -> {
                    if (user.getBalance() < input.getAmount()) {
                        insufficient.set(true);
                        return;
                    }
                    double newBalance = user.getBalance() - input.getAmount();
                    userRepository.updateBalance(input.getUuid(), newBalance);
                    playerStateRepository.setCachedBalance(input.getUuid(), newBalance, balanceCacheTtl);

                    TransactionModel transaction = TransactionModel.builder()
                        .id(UUID.randomUUID().toString())
                        .fromUuid(input.getUuid())
                        .toUuid(null)
                        .amount(input.getAmount())
                        .type(TransactionType.WITHDRAW)
                        .reason(input.getReason())
                        .timestamp(System.currentTimeMillis())
                        .status(TransactionStatus.SUCCESS)
                        .build();
                    transactionRepository.save(transaction);
                });
            }).get();

            if (insufficient.get()) {
                return ServiceResult.failure(MessageKey.ECONOMY_INSUFFICIENT_FUNDS);
            }
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
