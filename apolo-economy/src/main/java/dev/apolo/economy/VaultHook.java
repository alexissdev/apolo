package dev.apolo.economy;

import lombok.extern.slf4j.Slf4j;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

@Slf4j
public class VaultHook {
    private final Plugin plugin;
    private final ApoloEconomy apoloEconomy;
    private boolean vaultAvailable;

    public VaultHook(Plugin plugin, ApoloEconomy apoloEconomy) {
        this.plugin = plugin;
        this.apoloEconomy = apoloEconomy;
    }

    public void register() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            log.warn("Vault not found. Economy integration will be disabled.");
            vaultAvailable = false;
            return;
        }
        Bukkit.getServicesManager().register(Economy.class, apoloEconomy, plugin, ServicePriority.Normal);
        vaultAvailable = true;
        log.info("Vault economy registered successfully.");
    }

    public void unregister() {
        if (vaultAvailable) {
            Bukkit.getServicesManager().unregister(Economy.class, apoloEconomy);
            log.info("Vault economy unregistered.");
        }
    }

    public boolean isVaultAvailable() {
        return vaultAvailable;
    }
}
