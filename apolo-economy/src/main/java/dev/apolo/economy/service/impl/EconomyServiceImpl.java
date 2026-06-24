package dev.apolo.economy.service.impl;

import dev.apolo.api.event.ApoloEconomyDepositEvent;
import dev.apolo.api.event.ApoloEconomySetBalanceEvent;
import dev.apolo.api.event.ApoloEconomyTransferEvent;
import dev.apolo.api.event.ApoloEconomyWithdrawEvent;
import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.model.UserModel;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IEconomyService;
import dev.apolo.core.repository.interfaces.IPlayerStateRepository;
import dev.apolo.core.repository.interfaces.IUserRepository;
import dev.apolo.economy.sync.CrossServerEconomySync;
import dev.apolo.economy.usecase.DepositUseCase;
import dev.apolo.economy.usecase.GetBalanceUseCase;
import dev.apolo.economy.usecase.TransferUseCase;
import dev.apolo.economy.usecase.WithdrawUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class EconomyServiceImpl implements IEconomyService {
    private final DepositUseCase depositUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final TransferUseCase transferUseCase;
    private final GetBalanceUseCase getBalanceUseCase;
    private final IUserRepository userRepository;
    private final IPlayerStateRepository playerStateRepository;
    private final CrossServerEconomySync economySync;
    private final double maxBalance;
    private final int balanceCacheTtl;

    @Override
    public ServiceResult<Double> getBalance(String uuid) {
        return getBalanceUseCase.execute(GetBalanceUseCase.Input.builder().uuid(uuid).build());
    }

    @Override
    public ServiceResult<Double> getBalance(Player player) {
        return getBalance(player.getUniqueId().toString());
    }

    @Override
    public ServiceResult<Void> deposit(String uuid, double amount, String reason) {
        ServiceResult<Double> balResult = getBalance(uuid);
        double balanceBefore = balResult.isSuccess() ? balResult.getData().orElse(0.0) : 0.0;

        ApoloEconomyDepositEvent event = new ApoloEconomyDepositEvent(uuid, amount, balanceBefore, balanceBefore + amount, reason);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return ServiceResult.failure(MessageKey.NO_PERMISSION);
        }

        ServiceResult<Void> result = depositUseCase.execute(DepositUseCase.Input.builder()
            .uuid(uuid).amount(amount).reason(reason).build());

        if (result.isSuccess()) {
            publishUpdatedBalance(uuid);
        }
        return result;
    }

    @Override
    public ServiceResult<Void> withdraw(String uuid, double amount, String reason) {
        ServiceResult<Double> balResult = getBalance(uuid);
        double balanceBefore = balResult.isSuccess() ? balResult.getData().orElse(0.0) : 0.0;

        ApoloEconomyWithdrawEvent event = new ApoloEconomyWithdrawEvent(uuid, amount, balanceBefore, balanceBefore - amount, reason);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return ServiceResult.failure(MessageKey.NO_PERMISSION);
        }

        ServiceResult<Void> result = withdrawUseCase.execute(WithdrawUseCase.Input.builder()
            .uuid(uuid).amount(amount).reason(reason).build());

        if (result.isSuccess()) {
            publishUpdatedBalance(uuid);
        }
        return result;
    }

    @Override
    public ServiceResult<Void> transfer(String fromUuid, String toUuid, double amount, String reason) {
        ApoloEconomyTransferEvent event = new ApoloEconomyTransferEvent(fromUuid, toUuid, amount, reason);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return ServiceResult.failure(MessageKey.NO_PERMISSION);
        }

        ServiceResult<Void> result = transferUseCase.execute(TransferUseCase.Input.builder()
            .fromUuid(fromUuid).toUuid(toUuid).amount(amount).reason(reason).build());

        if (result.isSuccess()) {
            publishUpdatedBalance(fromUuid);
            publishUpdatedBalance(toUuid);
        }
        return result;
    }

    @Override
    public ServiceResult<Void> setBalance(String uuid, double amount) {
        ServiceResult<Double> balResult = getBalance(uuid);
        double oldBalance = balResult.isSuccess() ? balResult.getData().orElse(0.0) : 0.0;
        double newBalance = Math.max(0, Math.min(amount, maxBalance));

        ApoloEconomySetBalanceEvent event = new ApoloEconomySetBalanceEvent(uuid, oldBalance, newBalance);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return ServiceResult.failure(MessageKey.NO_PERMISSION);
        }

        try {
            userRepository.updateBalance(uuid, newBalance);
            playerStateRepository.setCachedBalance(uuid, newBalance, balanceCacheTtl);
            economySync.publishBalanceUpdate(uuid, newBalance);
            return ServiceResult.success();
        } catch (Exception e) {
            log.error("Error setting balance for {}", uuid, e);
            return ServiceResult.failure(MessageKey.ECONOMY_ACCOUNT_NOT_FOUND);
        }
    }

    @Override
    public ServiceResult<Boolean> hasEnough(String uuid, double amount) {
        ServiceResult<Double> balResult = getBalance(uuid);
        if (!balResult.isSuccess()) {
            return ServiceResult.failure(MessageKey.ECONOMY_ACCOUNT_NOT_FOUND);
        }
        return ServiceResult.success(balResult.getData().orElse(0.0) >= amount);
    }

    @Override
    public ServiceResult<List<UserModel>> getTopBalances(int limit) {
        try {
            List<UserModel> top = userRepository.findTopBalances(limit);
            return ServiceResult.success(top);
        } catch (Exception e) {
            log.error("Error fetching top balances", e);
            return ServiceResult.failure(MessageKey.ECONOMY_ACCOUNT_NOT_FOUND);
        }
    }

    // Lee el balance desde la caché Redis (ya actualizada por el use case) y publica el evento cross-server.
    private void publishUpdatedBalance(String uuid) {
        playerStateRepository.getCachedBalance(uuid)
            .ifPresent(balance -> economySync.publishBalanceUpdate(uuid, balance));
    }
}
