package dev.apolo.plugin.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import dev.apolo.api.service.IEconomyService;
import dev.apolo.api.service.IFlyService;
import dev.apolo.api.service.IGameModeService;
import dev.apolo.api.service.IGodModeService;
import dev.apolo.api.service.IRepairService;
import dev.apolo.api.service.ITransactionService;
import dev.apolo.api.service.ITpaService;
import dev.apolo.api.service.IUserService;
import dev.apolo.api.service.IWarpService;
import dev.apolo.core.repair.RepairStrategyFactory;
import dev.apolo.core.repository.interfaces.IPlayerStateRepository;
import dev.apolo.core.repository.interfaces.ITransactionRepository;
import dev.apolo.core.repository.interfaces.IUserRepository;
import dev.apolo.core.repository.interfaces.IWarpRepository;
import dev.apolo.core.service.impl.FlyServiceImpl;
import dev.apolo.core.service.impl.GameModeServiceImpl;
import dev.apolo.core.service.impl.GodModeServiceImpl;
import dev.apolo.core.service.impl.RepairServiceImpl;
import dev.apolo.core.service.impl.TpaServiceImpl;
import dev.apolo.core.service.impl.WarpServiceImpl;
import dev.apolo.api.service.IMessageService;
import dev.apolo.api.service.IPrivateMessageService;
import dev.apolo.core.service.impl.PrivateMessageServiceImpl;
import dev.apolo.core.usecase.RestorePlayerStateUseCase;
import dev.apolo.economy.ApoloEconomy;
import dev.apolo.economy.VaultHook;
import dev.apolo.economy.sync.CrossServerEconomySync;
import dev.apolo.plugin.sync.CrossServerCommandSpy;
import dev.apolo.plugin.sync.CrossServerPrivateMessaging;
import dev.apolo.redis.RedisManager;
import dev.apolo.redis.RedisSubscriber;
import dev.apolo.economy.service.impl.EconomyServiceImpl;
import dev.apolo.economy.service.impl.TransactionServiceImpl;
import dev.apolo.economy.service.impl.UserServiceImpl;
import dev.apolo.economy.usecase.CreateAccountUseCase;
import dev.apolo.economy.usecase.DepositUseCase;
import dev.apolo.economy.usecase.GetBalanceUseCase;
import dev.apolo.economy.usecase.TransferUseCase;
import dev.apolo.economy.usecase.WithdrawUseCase;
import dev.apolo.plugin.config.ApoloConfig;
import org.bukkit.plugin.Plugin;

public class EconomyModule extends AbstractModule {
    private final Plugin plugin;
    private final ApoloConfig config;

    public EconomyModule(Plugin plugin, ApoloConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Provides
    @Singleton
    public IGameModeService provideGameModeService() {
        return new GameModeServiceImpl();
    }

    @Provides
    @Singleton
    public ITpaService provideTpaService(IPlayerStateRepository playerStateRepository) {
        return new TpaServiceImpl(playerStateRepository, config.getTpaRequestTimeout(), config.getTpaCooldown());
    }

    @Provides
    @Singleton
    public IWarpService provideWarpService(IWarpRepository warpRepository,
                                           IPlayerStateRepository playerStateRepository) {
        return new WarpServiceImpl(warpRepository, playerStateRepository, config.getWarpCooldown());
    }

    @Provides
    @Singleton
    public IFlyService provideFlyService(IPlayerStateRepository playerStateRepository, IUserRepository userRepository) {
        return new FlyServiceImpl(playerStateRepository, userRepository);
    }

    @Provides
    @Singleton
    public IGodModeService provideGodModeService(IPlayerStateRepository playerStateRepository, IUserRepository userRepository) {
        return new GodModeServiceImpl(playerStateRepository, userRepository);
    }

    @Provides
    @Singleton
    public RepairStrategyFactory provideRepairStrategyFactory() {
        return new RepairStrategyFactory();
    }

    @Provides
    @Singleton
    public IRepairService provideRepairService(IPlayerStateRepository playerStateRepository,
                                               RepairStrategyFactory strategyFactory) {
        return new RepairServiceImpl(playerStateRepository, strategyFactory, config.getRepairCooldown());
    }

    @Provides
    @Singleton
    public CreateAccountUseCase provideCreateAccountUseCase(IUserRepository userRepository) {
        return new CreateAccountUseCase(userRepository, config.getStartingBalance());
    }

    @Provides
    @Singleton
    public IUserService provideUserService(IUserRepository userRepository,
                                           IPlayerStateRepository playerStateRepository,
                                           CreateAccountUseCase createAccountUseCase) {
        return new UserServiceImpl(userRepository, playerStateRepository, createAccountUseCase);
    }

    @Provides
    @Singleton
    public GetBalanceUseCase provideGetBalanceUseCase(IUserRepository userRepository,
                                                       IPlayerStateRepository playerStateRepository) {
        return new GetBalanceUseCase(userRepository, playerStateRepository, config.getBalanceCacheTtl());
    }

    @Provides
    @Singleton
    public DepositUseCase provideDepositUseCase(IUserRepository userRepository,
                                                 ITransactionRepository transactionRepository,
                                                 IPlayerStateRepository playerStateRepository) {
        return new DepositUseCase(userRepository, transactionRepository, playerStateRepository,
            config.getMaxBalance(), config.getBalanceCacheTtl());
    }

    @Provides
    @Singleton
    public WithdrawUseCase provideWithdrawUseCase(IUserRepository userRepository,
                                                   ITransactionRepository transactionRepository,
                                                   IPlayerStateRepository playerStateRepository) {
        return new WithdrawUseCase(userRepository, transactionRepository, playerStateRepository,
            config.getBalanceCacheTtl());
    }

    @Provides
    @Singleton
    public TransferUseCase provideTransferUseCase(IUserRepository userRepository,
                                                   ITransactionRepository transactionRepository,
                                                   IPlayerStateRepository playerStateRepository) {
        return new TransferUseCase(userRepository, transactionRepository, playerStateRepository,
            config.getMaxBalance(), config.getMinTransferAmount(), config.getBalanceCacheTtl());
    }

    @Provides
    @Singleton
    public CrossServerEconomySync provideCrossServerEconomySync(RedisManager redisManager,
                                                                  IPlayerStateRepository playerStateRepository,
                                                                  RedisSubscriber redisSubscriber) {
        return new CrossServerEconomySync(redisManager, playerStateRepository, redisSubscriber,
            config.getBalanceCacheTtl());
    }

    @Provides
    @Singleton
    public IEconomyService provideEconomyService(DepositUseCase depositUseCase,
                                                  WithdrawUseCase withdrawUseCase,
                                                  TransferUseCase transferUseCase,
                                                  GetBalanceUseCase getBalanceUseCase,
                                                  IUserRepository userRepository,
                                                  IPlayerStateRepository playerStateRepository,
                                                  CrossServerEconomySync economySync) {
        return new EconomyServiceImpl(depositUseCase, withdrawUseCase, transferUseCase, getBalanceUseCase,
            userRepository, playerStateRepository, economySync, config.getMaxBalance(), config.getBalanceCacheTtl());
    }

    @Provides
    @Singleton
    public ITransactionService provideTransactionService(ITransactionRepository transactionRepository) {
        return new TransactionServiceImpl(transactionRepository);
    }

    @Provides
    @Singleton
    public ApoloEconomy provideApoloEconomy(IEconomyService economyService, IUserRepository userRepository) {
        return new ApoloEconomy(economyService, userRepository,
            config.getCurrencyNameSingular(), config.getCurrencyNamePlural(), config.getDecimalDigits());
    }

    @Provides
    @Singleton
    public VaultHook provideVaultHook(ApoloEconomy apoloEconomy) {
        return new VaultHook(plugin, apoloEconomy);
    }

    @Provides
    @Singleton
    public RestorePlayerStateUseCase provideRestorePlayerStateUseCase(IFlyService flyService,
                                                                       IGodModeService godModeService) {
        return new RestorePlayerStateUseCase(flyService, godModeService);
    }

    @Provides
    @Singleton
    public CrossServerPrivateMessaging provideCrossServerPrivateMessaging(RedisManager redisManager) {
        return new CrossServerPrivateMessaging(redisManager);
    }

    @Provides
    @Singleton
    public CrossServerCommandSpy provideCrossServerCommandSpy(RedisManager redisManager) {
        return new CrossServerCommandSpy(redisManager, config.getServerId());
    }

    @Provides
    @Singleton
    public IPrivateMessageService providePrivateMessageService(IPlayerStateRepository playerStateRepository,
                                                                IMessageService messageService,
                                                                CrossServerPrivateMessaging crossServerPrivateMessaging) {
        return new PrivateMessageServiceImpl(
            playerStateRepository,
            messageService,
            crossServerPrivateMessaging::publish
        );
    }
}
