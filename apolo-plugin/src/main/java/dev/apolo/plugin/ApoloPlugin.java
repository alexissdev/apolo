package dev.apolo.plugin;

import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.apolo.api.ApoloAPI;
import dev.apolo.api.ApoloServicesProvider;
import dev.apolo.api.service.IEconomyService;
import dev.apolo.api.service.IFlyService;
import dev.apolo.api.service.IGameModeService;
import dev.apolo.api.service.IGodModeService;
import dev.apolo.api.service.IMessageService;
import dev.apolo.api.service.IRepairService;
import dev.apolo.api.service.ITransactionService;
import dev.apolo.api.service.ITpaService;
import dev.apolo.api.service.IUserService;
import dev.apolo.api.service.IWarpService;
import dev.apolo.commands.base.CommandRegistry;
import dev.apolo.commands.impl.BalanceCommand;
import dev.apolo.commands.impl.BaltopCommand;
import dev.apolo.commands.impl.DelWarpCommand;
import dev.apolo.commands.impl.EcoCommand;
import dev.apolo.commands.impl.FlyCommand;
import dev.apolo.commands.impl.GameModeCommand;
import dev.apolo.commands.impl.GodModeCommand;
import dev.apolo.commands.impl.PayCommand;
import dev.apolo.commands.impl.RepairCommand;
import dev.apolo.commands.impl.SetWarpCommand;
import dev.apolo.commands.impl.TpAcceptCommand;
import dev.apolo.commands.impl.TpDenyCommand;
import dev.apolo.commands.impl.TpHereCommand;
import dev.apolo.commands.impl.TpaCancelCommand;
import dev.apolo.commands.impl.TpaCommand;
import dev.apolo.commands.impl.MsgCommand;
import dev.apolo.commands.impl.ReplyCommand;
import dev.apolo.commands.impl.CommandSpyCommand;
import dev.apolo.commands.impl.SocialSpyCommand;
import dev.apolo.listeners.impl.CommandSpyListener;
import dev.apolo.plugin.sync.CrossServerCommandSpy;
import dev.apolo.commands.impl.WarpCommand;
import dev.apolo.commands.impl.WarpsCommand;
import dev.apolo.api.service.IPrivateMessageService;
import dev.apolo.plugin.sync.CrossServerPrivateMessaging;
import dev.apolo.database.MongoManager;
import dev.apolo.economy.VaultHook;
import dev.apolo.economy.sync.CrossServerEconomySync;
import dev.apolo.listeners.base.ListenerRegistry;
import dev.apolo.listeners.impl.GodModeListener;
import dev.apolo.listeners.impl.PlayerSessionListener;
import dev.apolo.listeners.impl.TpaListener;
import dev.apolo.plugin.config.ApoloConfig;
import dev.apolo.plugin.config.ConfigLoader;
import dev.apolo.plugin.di.ApoloModule;
import dev.apolo.redis.RedisManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.plugin.java.JavaPlugin;

@Slf4j
public class ApoloPlugin extends JavaPlugin {
    @Getter
    private static ApoloPlugin instance;
    private Injector injector;
    private ApoloConfig config;
    private VaultHook vaultHook;
    private CrossServerEconomySync economySync;
    private CrossServerPrivateMessaging privateMessaging;
    private CrossServerCommandSpy commandSpy;

    @Override
    public void onEnable() {
        instance = this;
        printBanner();

        ConfigLoader configLoader = new ConfigLoader(this);
        config = configLoader.load();

        injector = Guice.createInjector(new ApoloModule(this, config));

        setupAPI();
        registerCommands();
        registerListeners();
        setupVault();
        setupEconomySync();
        setupPrivateMessaging();
        setupCommandSpy();

        log.info("Plugin enabled successfully.");
    }

    private void printBanner() {
        String v = getDescription().getVersion();
        org.bukkit.Bukkit.getConsoleSender().sendMessage("");
        org.bukkit.Bukkit.getConsoleSender().sendMessage("§3    ___  ____   ___  _     ___  ");
        org.bukkit.Bukkit.getConsoleSender().sendMessage("§3   / _ \\|  _ \\ / _ \\| |   / _ \\ ");
        org.bukkit.Bukkit.getConsoleSender().sendMessage("§3  / /_\\ \\ |_) | | | | |  | | | |");
        org.bukkit.Bukkit.getConsoleSender().sendMessage("§3 /  ___  \\  __/| |_| | |__| |_| |");
        org.bukkit.Bukkit.getConsoleSender().sendMessage("§3/_/   \\_\\_|    \\___/|_____\\___/ ");
        org.bukkit.Bukkit.getConsoleSender().sendMessage("§8  ─────────────────────────────");
        org.bukkit.Bukkit.getConsoleSender().sendMessage("§7  Version §b" + v + "  §8│  §7by §bAlexisDev");
        org.bukkit.Bukkit.getConsoleSender().sendMessage("§8  ─────────────────────────────");
        org.bukkit.Bukkit.getConsoleSender().sendMessage("");
    }

    private void setupAPI() {
        IGameModeService gameModeService = injector.getInstance(IGameModeService.class);
        ITpaService tpaService = injector.getInstance(ITpaService.class);
        IWarpService warpService = injector.getInstance(IWarpService.class);
        IFlyService flyService = injector.getInstance(IFlyService.class);
        IGodModeService godModeService = injector.getInstance(IGodModeService.class);
        IRepairService repairService = injector.getInstance(IRepairService.class);
        IMessageService messageService = injector.getInstance(IMessageService.class);
        IEconomyService economyService = injector.getInstance(IEconomyService.class);
        IUserService userService = injector.getInstance(IUserService.class);
        ITransactionService transactionService = injector.getInstance(ITransactionService.class);

        ApoloAPI.setProvider(new ApoloServicesProvider() {
            @Override public IGameModeService getGameModeService() { return gameModeService; }
            @Override public ITpaService getTpaService() { return tpaService; }
            @Override public IWarpService getWarpService() { return warpService; }
            @Override public IFlyService getFlyService() { return flyService; }
            @Override public IGodModeService getGodModeService() { return godModeService; }
            @Override public IRepairService getRepairService() { return repairService; }
            @Override public IMessageService getMessageService() { return messageService; }
            @Override public IEconomyService getEconomyService() { return economyService; }
            @Override public IUserService getUserService() { return userService; }
            @Override public ITransactionService getTransactionService() { return transactionService; }
        });
    }

    private void registerCommands() {
        CommandRegistry registry = injector.getInstance(CommandRegistry.class);
        registry.register("gamemode", injector.getInstance(GameModeCommand.class));
        registry.register("tpa", injector.getInstance(TpaCommand.class));
        registry.register("tphere", injector.getInstance(TpHereCommand.class));
        registry.register("tpaccept", injector.getInstance(TpAcceptCommand.class));
        registry.register("tpadeny", injector.getInstance(TpDenyCommand.class));
        registry.register("tpacancel", injector.getInstance(TpaCancelCommand.class));
        registry.register("warp", injector.getInstance(WarpCommand.class));
        registry.register("setwarp", injector.getInstance(SetWarpCommand.class));
        registry.register("delwarp", injector.getInstance(DelWarpCommand.class));
        registry.register("warps", injector.getInstance(WarpsCommand.class));
        registry.register("fly", injector.getInstance(FlyCommand.class));
        registry.register("godmode", injector.getInstance(GodModeCommand.class));
        registry.register("repair", injector.getInstance(RepairCommand.class));
        registry.register("balance", injector.getInstance(BalanceCommand.class));
        registry.register("pay", injector.getInstance(PayCommand.class));
        registry.register("eco", injector.getInstance(EcoCommand.class));
        registry.register("baltop", injector.getInstance(BaltopCommand.class));
        registry.register("msg", injector.getInstance(MsgCommand.class));
        registry.register("reply", injector.getInstance(ReplyCommand.class));
        registry.register("socialspy", injector.getInstance(SocialSpyCommand.class));
        registry.register("commandspy", injector.getInstance(CommandSpyCommand.class));
    }

    private void registerListeners() {
        ListenerRegistry registry = injector.getInstance(ListenerRegistry.class);
        registry.register(injector.getInstance(TpaListener.class));
        registry.register(injector.getInstance(GodModeListener.class));
        registry.register(injector.getInstance(PlayerSessionListener.class));
        registry.register(injector.getInstance(CommandSpyListener.class));
    }

    private void setupVault() {
        vaultHook = injector.getInstance(VaultHook.class);
        vaultHook.register();
    }

    private void setupEconomySync() {
        economySync = injector.getInstance(CrossServerEconomySync.class);
        economySync.startListening();
    }

    private void setupPrivateMessaging() {
        privateMessaging = injector.getInstance(CrossServerPrivateMessaging.class);
        IPrivateMessageService privateMessageService = injector.getInstance(IPrivateMessageService.class);
        privateMessaging.startListening(privateMessageService);
    }

    private void setupCommandSpy() {
        commandSpy = injector.getInstance(CrossServerCommandSpy.class);
        CommandSpyListener commandSpyListener = injector.getInstance(CommandSpyListener.class);
        commandSpy.startListening(commandSpyListener::deliverLocally);
    }

    @Override
    public void onDisable() {
        if (commandSpy != null) {
            commandSpy.stop();
        }
        if (privateMessaging != null) {
            privateMessaging.stop();
        }
        if (economySync != null) {
            economySync.stop();
        }
        if (vaultHook != null) {
            vaultHook.unregister();
        }
        try {
            RedisManager redisManager = injector.getInstance(RedisManager.class);
            redisManager.shutdown();
        } catch (Exception e) {
            log.error("Error shutting down Redis", e);
        }
        try {
            MongoManager mongoManager = injector.getInstance(MongoManager.class);
            mongoManager.shutdown();
        } catch (Exception e) {
            log.error("Error shutting down MongoDB", e);
        }
        log.info("Apolo plugin disabled.");
    }
}
