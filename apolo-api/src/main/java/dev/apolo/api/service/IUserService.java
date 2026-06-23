package dev.apolo.api.service;

import dev.apolo.api.model.UserModel;
import dev.apolo.api.result.ServiceResult;
import org.bukkit.entity.Player;

public interface IUserService {
    ServiceResult<UserModel> getOrCreateUser(Player player);
    ServiceResult<UserModel> getUser(String uuid);
    ServiceResult<UserModel> getUser(Player player);
    ServiceResult<Void> updateUser(UserModel user);
    ServiceResult<Void> syncState(Player player);
}
