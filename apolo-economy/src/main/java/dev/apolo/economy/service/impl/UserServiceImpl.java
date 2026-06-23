package dev.apolo.economy.service.impl;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.model.UserModel;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IUserService;
import dev.apolo.core.repository.interfaces.IPlayerStateRepository;
import dev.apolo.core.repository.interfaces.IUserRepository;
import dev.apolo.economy.usecase.CreateAccountUseCase;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final IUserRepository userRepository;
    private final IPlayerStateRepository playerStateRepository;
    private final CreateAccountUseCase createAccountUseCase;

    @Override
    public ServiceResult<UserModel> getOrCreateUser(Player player) {
        try {
            boolean exists = userRepository.exists(player.getUniqueId().toString()).get();
            if (exists) {
                return userRepository.findByUuid(player.getUniqueId().toString()).get()
                    .map(ServiceResult::success)
                    .orElse(createAccountUseCase.execute(CreateAccountUseCase.Input.builder().player(player).build()));
            }
            return createAccountUseCase.execute(CreateAccountUseCase.Input.builder().player(player).build());
        } catch (Exception e) {
            return ServiceResult.failure(MessageKey.ECONOMY_ACCOUNT_NOT_FOUND);
        }
    }

    @Override
    public ServiceResult<UserModel> getUser(String uuid) {
        try {
            return userRepository.findByUuid(uuid).get()
                .map(ServiceResult::success)
                .orElse(ServiceResult.notFound(MessageKey.ECONOMY_ACCOUNT_NOT_FOUND));
        } catch (Exception e) {
            return ServiceResult.failure(MessageKey.ECONOMY_ACCOUNT_NOT_FOUND);
        }
    }

    @Override
    public ServiceResult<UserModel> getUser(Player player) {
        return getUser(player.getUniqueId().toString());
    }

    @Override
    public ServiceResult<Void> updateUser(UserModel user) {
        try {
            userRepository.update(user).get();
            return ServiceResult.success();
        } catch (Exception e) {
            return ServiceResult.failure(MessageKey.ECONOMY_ACCOUNT_NOT_FOUND);
        }
    }

    @Override
    public ServiceResult<Void> syncState(Player player) {
        try {
            String uuid = player.getUniqueId().toString();
            userRepository.updateLastSeen(uuid, System.currentTimeMillis()).get();
            userRepository.updateFlyState(uuid, player.getAllowFlight()).get();
            return ServiceResult.success();
        } catch (Exception e) {
            return ServiceResult.failure(MessageKey.ECONOMY_ACCOUNT_NOT_FOUND);
        }
    }
}
