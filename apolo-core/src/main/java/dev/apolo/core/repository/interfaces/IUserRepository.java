package dev.apolo.core.repository.interfaces;

import dev.apolo.api.model.UserModel;

import java.util.List;
import java.util.Optional;

public interface IUserRepository {
    Optional<UserModel> findByUuid(String uuid);
    Optional<UserModel> findByUsername(String username);
    UserModel save(UserModel user);
    UserModel update(UserModel user);
    boolean exists(String uuid);
    void updateBalance(String uuid, double newBalance);
    void updateLastSeen(String uuid, long timestamp);
    void updateFlyState(String uuid, boolean enabled);
    void updateGodState(String uuid, boolean enabled);
    List<UserModel> findTopBalances(int limit);
}
