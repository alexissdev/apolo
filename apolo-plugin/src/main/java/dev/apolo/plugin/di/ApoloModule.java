package dev.apolo.plugin.di;

import com.google.inject.AbstractModule;
import dev.apolo.plugin.config.ApoloConfig;
import org.bukkit.plugin.Plugin;

public class ApoloModule extends AbstractModule {
    private final Plugin plugin;
    private final ApoloConfig config;

    public ApoloModule(Plugin plugin, ApoloConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    protected void configure() {
        install(new DatabaseModule(config));
        install(new RedisModule(config));
        install(new MessagingModule(plugin));
        install(new RepositoryModule());
        install(new EconomyModule(plugin, config));
        install(new CommandModule(plugin));
        install(new ListenerModule(plugin, config));
    }
}
