package dev.apolo.messaging.provider;

import dev.apolo.api.exception.ApoloMessageKeyNotFoundException;
import dev.apolo.api.messaging.MessageKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class YamlMessageProvider implements MessageProvider {

    private final Plugin plugin;
    private YamlConfiguration config;

    public YamlMessageProvider(Plugin plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
        InputStream defStream = plugin.getResource("messages.yml");
        if (defStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defStream, StandardCharsets.UTF_8));
            config.setDefaults(defConfig);
        }
    }

    @Override
    public String getRaw(MessageKey key) {
        String value = config.getString(key.getPath());
        if (value == null) {
            throw new ApoloMessageKeyNotFoundException(key.getPath());
        }
        return value;
    }

    @Override
    public String getRawByPath(String path) {
        String value = config.getString(path);
        return value != null ? value : "";
    }

    @Override
    public void reload() {
        load();
    }
}
