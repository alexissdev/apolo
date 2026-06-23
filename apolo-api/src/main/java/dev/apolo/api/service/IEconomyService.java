package dev.apolo.api.service;

import dev.apolo.api.model.UserModel;
import dev.apolo.api.result.ServiceResult;
import org.bukkit.entity.Player;

import java.util.List;

public interface IEconomyService {
    ServiceResult<Double> getBalance(String uuid);
    ServiceResult<Double> getBalance(Player player);
    ServiceResult<Void> deposit(String uuid, double amount, String reason);
    ServiceResult<Void> withdraw(String uuid, double amount, String reason);
    ServiceResult<Void> transfer(String fromUuid, String toUuid, double amount, String reason);
    ServiceResult<Void> setBalance(String uuid, double amount);
    ServiceResult<Boolean> hasEnough(String uuid, double amount);
    ServiceResult<List<UserModel>> getTopBalances(int limit);
}
