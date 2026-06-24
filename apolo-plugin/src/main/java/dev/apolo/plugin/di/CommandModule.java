package dev.apolo.plugin.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import dev.apolo.api.service.IEconomyService;
import dev.apolo.api.service.IFlyService;
import dev.apolo.api.service.IGameModeService;
import dev.apolo.api.service.IGodModeService;
import dev.apolo.api.service.IMessageService;
import dev.apolo.api.service.IRepairService;
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
import dev.apolo.commands.impl.TpaCommand;
import dev.apolo.api.service.IPrivateMessageService;
import dev.apolo.commands.impl.MsgCommand;
import dev.apolo.commands.impl.ReplyCommand;
import dev.apolo.commands.impl.CommandSpyCommand;
import dev.apolo.commands.impl.SocialSpyCommand;
import dev.apolo.core.repository.interfaces.IPlayerStateRepository;
import dev.apolo.commands.impl.WarpCommand;
import dev.apolo.commands.impl.WarpsCommand;
import dev.apolo.plugin.config.ApoloConfig;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandModule extends AbstractModule {
    private final JavaPlugin plugin;
    private final ApoloConfig config;

    public CommandModule(JavaPlugin plugin, ApoloConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Provides
    @Singleton
    public CommandRegistry provideCommandRegistry() {
        return new CommandRegistry(plugin);
    }

    @Provides
    @Singleton
    public GameModeCommand provideGameModeCommand(IMessageService msg, IGameModeService gm) {
        return new GameModeCommand(msg, gm);
    }

    @Provides
    @Singleton
    public TpaCommand provideTpaCommand(IMessageService msg, ITpaService tpa) {
        return new TpaCommand(msg, tpa);
    }

    @Provides
    @Singleton
    public TpHereCommand provideTpHereCommand(IMessageService msg, ITpaService tpa) {
        return new TpHereCommand(msg, tpa);
    }

    @Provides
    @Singleton
    public TpAcceptCommand provideTpAcceptCommand(IMessageService msg, ITpaService tpa) {
        return new TpAcceptCommand(msg, tpa);
    }

    @Provides
    @Singleton
    public TpDenyCommand provideTpDenyCommand(IMessageService msg, ITpaService tpa) {
        return new TpDenyCommand(msg, tpa);
    }

    @Provides
    @Singleton
    public WarpCommand provideWarpCommand(IMessageService msg, IWarpService warp) {
        return new WarpCommand(msg, warp);
    }

    @Provides
    @Singleton
    public SetWarpCommand provideSetWarpCommand(IMessageService msg, IWarpService warp) {
        return new SetWarpCommand(msg, warp);
    }

    @Provides
    @Singleton
    public DelWarpCommand provideDelWarpCommand(IMessageService msg, IWarpService warp) {
        return new DelWarpCommand(msg, warp);
    }

    @Provides
    @Singleton
    public WarpsCommand provideWarpsCommand(IMessageService msg, IWarpService warp) {
        return new WarpsCommand(msg, warp, config.getWarpsPerPage());
    }

    @Provides
    @Singleton
    public FlyCommand provideFlyCommand(IMessageService msg, IFlyService fly) {
        return new FlyCommand(msg, fly);
    }

    @Provides
    @Singleton
    public GodModeCommand provideGodModeCommand(IMessageService msg, IGodModeService god) {
        return new GodModeCommand(msg, god);
    }

    @Provides
    @Singleton
    public RepairCommand provideRepairCommand(IMessageService msg, IRepairService repair) {
        return new RepairCommand(msg, repair);
    }

    @Provides
    @Singleton
    public BalanceCommand provideBalanceCommand(IMessageService msg, IEconomyService eco, IUserService user) {
        return new BalanceCommand(msg, eco, user);
    }

    @Provides
    @Singleton
    public PayCommand providePayCommand(IMessageService msg, IEconomyService eco) {
        return new PayCommand(msg, eco);
    }

    @Provides
    @Singleton
    public EcoCommand provideEcoCommand(IMessageService msg, IEconomyService eco, IUserService user) {
        return new EcoCommand(msg, eco, user);
    }

    @Provides
    @Singleton
    public BaltopCommand provideBaltopCommand(IMessageService msg, IEconomyService eco) {
        return new BaltopCommand(msg, eco);
    }

    @Provides
    @Singleton
    public MsgCommand provideMsgCommand(IMessageService msg, IPrivateMessageService privateMsg) {
        return new MsgCommand(msg, privateMsg);
    }

    @Provides
    @Singleton
    public ReplyCommand provideReplyCommand(IMessageService msg, IPrivateMessageService privateMsg) {
        return new ReplyCommand(msg, privateMsg);
    }

    @Provides
    @Singleton
    public SocialSpyCommand provideSocialSpyCommand(IMessageService msg, IPlayerStateRepository playerState) {
        return new SocialSpyCommand(msg, playerState);
    }

    @Provides
    @Singleton
    public CommandSpyCommand provideCommandSpyCommand(IMessageService msg, IPlayerStateRepository playerState) {
        return new CommandSpyCommand(msg, playerState);
    }
}
