package dev.apolo.economy.usecase;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.model.PlayerRank;
import dev.apolo.api.model.UserModel;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.core.repository.interfaces.IUserRepository;
import dev.apolo.core.usecase.UseCase;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.bukkit.entity.Player;

import java.util.Collections;

@RequiredArgsConstructor
public class CreateAccountUseCase implements UseCase<CreateAccountUseCase.Input, UserModel> {
    private final IUserRepository userRepository;
    private final double startingBalance;

    @Override
    public ServiceResult<UserModel> execute(Input input) {
        try {
            String uuid = input.getPlayer().getUniqueId().toString();
            if (userRepository.exists(uuid)) {
                return userRepository.findByUuid(uuid)
                    .map(ServiceResult::success)
                    .orElse(ServiceResult.failure(MessageKey.ECONOMY_ACCOUNT_NOT_FOUND));
            }

            UserModel newUser = UserModel.builder()
                .uuid(uuid)
                .username(input.getPlayer().getName())
                .lastKnownName(input.getPlayer().getName())
                .balance(startingBalance)
                .firstJoin(System.currentTimeMillis())
                .lastSeen(System.currentTimeMillis())
                .flyEnabled(false)
                .godModeEnabled(false)
                .rank(PlayerRank.DEFAULT)
                .metadata(Collections.emptyMap())
                .build();

            userRepository.save(newUser);
            return ServiceResult.success(newUser);
        } catch (Exception e) {
            return ServiceResult.failure(MessageKey.ECONOMY_ACCOUNT_NOT_FOUND);
        }
    }

    @Value
    @Builder
    public static class Input {
        Player player;
    }
}
