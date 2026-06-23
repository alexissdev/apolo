package dev.apolo.plugin.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import dev.apolo.api.service.IGodModeService;
import dev.apolo.api.service.IMessageService;
import dev.apolo.api.service.ITpaService;
import dev.apolo.api.service.IUserService;
import dev.apolo.core.repository.interfaces.IPlayerStateRepository;
import dev.apolo.core.usecase.RestorePlayerStateUseCase;
import dev.apolo.listeners.base.ListenerRegistry;
import dev.apolo.listeners.impl.CommandSpyListener;
import dev.apolo.listeners.impl.GodModeListener;
import dev.apolo.listeners.impl.PlayerSessionListener;
import dev.apolo.listeners.impl.TpaListener;
import dev.apolo.plugin.config.ApoloConfig;
import dev.apolo.plugin.sync.CrossServerCommandSpy;
import org.bukkit.plugin.Plugin;

public class ListenerModule extends AbstractModule {
    private final Plugin plugin;
    private final ApoloConfig config;

    public ListenerModule(Plugin plugin, ApoloConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Provides
    @Singleton
    public ListenerRegistry provideListenerRegistry() {
        return new ListenerRegistry(plugin);
    }

    @Provides
    @Singleton
    public TpaListener provideTpaListener(ITpaService tpaService, IMessageService messageService) {
        return new TpaListener(tpaService, messageService);
    }

    @Provides
    @Singleton
    public GodModeListener provideGodModeListener(IGodModeService godModeService) {
        return new GodModeListener(godModeService);
    }

    @Provides
    @Singleton
    public PlayerSessionListener providePlayerSessionListener(IUserService userService,
                                                               IMessageService messageService,
                                                               RestorePlayerStateUseCase restoreUseCase,
                                                               IPlayerStateRepository playerStateRepository) {
        return new PlayerSessionListener(userService, messageService, restoreUseCase, playerStateRepository);
    }

    @Provides
    @Singleton
    public CommandSpyListener provideCommandSpyListener(IPlayerStateRepository playerStateRepository,
                                                         IMessageService messageService,
                                                         CrossServerCommandSpy crossServerCommandSpy) {
        return new CommandSpyListener(playerStateRepository, messageService,
            crossServerCommandSpy::publish, config.getServerId());
    }
}
