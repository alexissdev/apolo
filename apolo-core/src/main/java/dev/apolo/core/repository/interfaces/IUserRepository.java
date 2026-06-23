package dev.apolo.core.repository.interfaces;

import dev.apolo.api.model.UserModel;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface IUserRepository {
    CompletableFuture<Optional<UserModel>> findByUuid(String uuid);
    CompletableFuture<Optional<UserModel>> findByUsername(String username);
    CompletableFuture<UserModel> save(UserModel user);
    CompletableFuture<UserModel> update(UserModel user);
    CompletableFuture<Boolean> exists(String uuid);
    CompletableFuture<Void> updateBalance(String uuid, double newBalance);
    CompletableFuture<Void> updateLastSeen(String uuid, long timestamp);
    CompletableFuture<Void> updateFlyState(String uuid, boolean enabled);
    CompletableFuture<Void> updateGodState(String uuid, boolean enabled);
    CompletableFuture<List<UserModel>> findTopBalances(int limit);
}
