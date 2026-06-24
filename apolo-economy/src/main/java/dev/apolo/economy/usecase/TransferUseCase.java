package dev.apolo.economy.usecase;

import dev.apolo.api.enums.TransactionStatus;
import dev.apolo.api.enums.TransactionType;
import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.model.TransactionModel;
import dev.apolo.api.model.UserModel;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.core.repository.interfaces.IPlayerStateRepository;
import dev.apolo.core.repository.interfaces.ITransactionRepository;
import dev.apolo.core.repository.interfaces.IUserRepository;
import dev.apolo.core.usecase.UseCase;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class TransferUseCase implements UseCase<TransferUseCase.Input, Void> {
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
            Optional<UserModel> fromOpt = userRepository.findByUuid(input.getFromUuid());
            Optional<UserModel> toOpt = userRepository.findByUuid(input.getToUuid());

            if (fromOpt.isEmpty()) {
                return ServiceResult.failure(MessageKey.ECONOMY_ACCOUNT_NOT_FOUND);
            }
            if (toOpt.isEmpty()) {
                return ServiceResult.failure(MessageKey.ECONOMY_ACCOUNT_NOT_FOUND);
            }

            UserModel from = fromOpt.get();
            UserModel to = toOpt.get();

            if (from.getBalance() < input.getAmount()) {
                return ServiceResult.failure(MessageKey.ECONOMY_INSUFFICIENT_FUNDS);
            }

            double newFromBalance = from.getBalance() - input.getAmount();
            double newToBalance = Math.min(to.getBalance() + input.getAmount(), maxBalance);

            userRepository.updateBalance(input.getFromUuid(), newFromBalance);
            userRepository.updateBalance(input.getToUuid(), newToBalance);
            playerStateRepository.setCachedBalance(input.getFromUuid(), newFromBalance, balanceCacheTtl);
            playerStateRepository.setCachedBalance(input.getToUuid(), newToBalance, balanceCacheTtl);

            TransactionModel transaction = TransactionModel.builder()
                .id(UUID.randomUUID().toString())
                .fromUuid(input.getFromUuid())
                .toUuid(input.getToUuid())
                .amount(input.getAmount())
                .type(TransactionType.TRANSFER)
                .reason(input.getReason())
                .timestamp(System.currentTimeMillis())
                .status(TransactionStatus.SUCCESS)
                .build();
            transactionRepository.save(transaction);

            return ServiceResult.success();
        } catch (Exception e) {
            return ServiceResult.failure(MessageKey.ECONOMY_ACCOUNT_NOT_FOUND);
        }
    }

    @Value
    @Builder
    public static class Input {
        String fromUuid;
        String toUuid;
        double amount;
        String reason;
    }
}
