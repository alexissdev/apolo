package dev.apolo.plugin.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import dev.apolo.api.service.IMessageService;
import dev.apolo.messaging.MessageServiceImpl;
import dev.apolo.messaging.formatter.ColorFormatter;
import dev.apolo.messaging.placeholder.PlaceholderResolver;
import dev.apolo.messaging.placeholder.impl.StandardPlaceholderResolver;
import dev.apolo.messaging.provider.MessageProvider;
import dev.apolo.messaging.provider.YamlMessageProvider;
import org.bukkit.plugin.Plugin;

public class MessagingModule extends AbstractModule {
    private final Plugin plugin;

    public MessagingModule(Plugin plugin) {
        this.plugin = plugin;
    }

    @Provides
    @Singleton
    public ColorFormatter provideColorFormatter() {
        return new ColorFormatter();
    }

    @Provides
    @Singleton
    public PlaceholderResolver providePlaceholderResolver() {
        return new StandardPlaceholderResolver();
    }

    @Provides
    @Singleton
    public MessageProvider provideMessageProvider() {
        return new YamlMessageProvider(plugin);
    }

    @Provides
    @Singleton
    public IMessageService provideMessageService(MessageProvider provider,
                                                  PlaceholderResolver resolver,
                                                  ColorFormatter formatter) {
        return new MessageServiceImpl(provider, resolver, formatter);
    }
}
