package dev.apolo.economy;

import dev.apolo.api.service.IEconomyService;
import dev.apolo.core.repository.interfaces.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ApoloEconomy implements Economy {
    private final IEconomyService economyService;
    private final IUserRepository userRepository;
    private final String currencyNameSingular;
    private final String currencyNamePlural;
    private final int decimalDigits;

    @Override
    public boolean isEnabled() { return true; }

    @Override
    public String getName() { return "Apolo"; }

    @Override
    public boolean hasBankSupport() { return false; }

    @Override
    public int fractionalDigits() { return decimalDigits; }

    @Override
    public String format(double amount) {
        BigDecimal bd = new BigDecimal(amount).setScale(decimalDigits, RoundingMode.HALF_UP);
        return bd.toPlainString() + " " + (amount == 1.0 ? currencyNameSingular : currencyNamePlural);
    }

    @Override
    public String currencyNamePlural() { return currencyNamePlural; }

    @Override
    public String currencyNameSingular() { return currencyNameSingular; }

    @Override
    public boolean hasAccount(String playerName) {
        try {
            return userRepository.findByUsername(playerName).isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        try {
            return userRepository.exists(player.getUniqueId().toString());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    @Override
    public double getBalance(String playerName) {
        try {
            return userRepository.findByUsername(playerName)
                .map(user -> {
                    var result = economyService.getBalance(user.getUuid());
                    return result.isSuccess() ? result.getData().orElse(0.0) : 0.0;
                }).orElse(0.0);
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        var result = economyService.getBalance(player.getUniqueId().toString());
        return result.isSuccess() ? result.getData().orElse(0.0) : 0.0;
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        try {
            String uuid = userRepository.findByUsername(playerName)
                .map(dev.apolo.api.model.UserModel::getUuid).orElse(null);
            if (uuid == null) {
                return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player not found");
            }
            var result = economyService.withdraw(uuid, amount, "Vault withdrawal");
            if (result.isSuccess()) {
                double newBalance = getBalance(playerName);
                return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
            }
            return new EconomyResponse(0, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
        } catch (Exception e) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, e.getMessage());
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        var result = economyService.withdraw(player.getUniqueId().toString(), amount, "Vault withdrawal");
        if (result.isSuccess()) {
            return new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, "");
        }
        return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        try {
            String uuid = userRepository.findByUsername(playerName)
                .map(dev.apolo.api.model.UserModel::getUuid).orElse(null);
            if (uuid == null) {
                return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player not found");
            }
            var result = economyService.deposit(uuid, amount, "Vault deposit");
            if (result.isSuccess()) {
                return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, "");
            }
            return new EconomyResponse(0, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Deposit failed");
        } catch (Exception e) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, e.getMessage());
        }
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        var result = economyService.deposit(player.getUniqueId().toString(), amount, "Vault deposit");
        if (result.isSuccess()) {
            return new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, "");
        }
        return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, "Deposit failed");
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public boolean createPlayerAccount(String playerName) { return false; }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) { return false; }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) { return false; }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) { return false; }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }

    @Override
    public List<String> getBanks() { return Collections.emptyList(); }
}
