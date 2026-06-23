package dev.apolo.economy.usecase;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.core.repository.interfaces.IPlayerStateRepository;
import dev.apolo.core.repository.interfaces.IUserRepository;
import dev.apolo.core.usecase.UseCase;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Optional;

@RequiredArgsConstructor
public class GetBalanceUseCase implements UseCase<GetBalanceUseCase.Input, Double> {
    private final IUserRepository userRepository;
    private final IPlayerStateRepository playerStateRepository;
    private final int balanceCacheTtl;

    @Override
    public ServiceResult<Double> execute(Input input) {
        Optional<Double> cached = playerStateRepository.getCachedBalance(input.getUuid());
        if (cached.isPresent()) {
            return ServiceResult.success(cached.get());
        }

        try {
            return userRepository.findByUuid(input.getUuid()).get()
                .map(user -> {
                    playerStateRepository.setCachedBalance(input.getUuid(), user.getBalance(), balanceCacheTtl);
                    return ServiceResult.success(user.getBalance());
                })
                .orElse(ServiceResult.notFound(MessageKey.ECONOMY_ACCOUNT_NOT_FOUND));
        } catch (Exception e) {
            return ServiceResult.failure(MessageKey.ECONOMY_ACCOUNT_NOT_FOUND);
        }
    }

    @Value
    @Builder
    public static class Input {
        String uuid;
    }
}
