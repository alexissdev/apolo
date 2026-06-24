package dev.apolo.plugin.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

@Getter
public class ApoloConfig {
    private String redisHost;
    private int redisPort;
    private String redisPassword;
    private int redisDatabase;
    private int redisMaxTotal;
    private int redisMaxIdle;
    private int redisMinIdle;

    private String mongoUri;
    private String mongoDatabase;
    private int mongoConnectionTimeout;
    private int mongoSocketTimeout;
    private int mongoMaxPoolSize;

    private int tpaRequestTimeout;
    private int tpaCooldown;

    private int repairCooldown;

    private boolean flyRestoreOnJoin;

    private boolean godmodeRestoreOnJoin;

    private int warpsPerPage;
    private int warpCooldown;

    private double startingBalance;
    private String currencySymbol;
    private String currencyNameSingular;
    private String currencyNamePlural;
    private int decimalDigits;
    private double maxBalance;
    private double minTransferAmount;

    private int balanceCacheTtl;
    private int userCacheTtl;

    private String messagesFile;
    private String serverId;

    public void load(Plugin plugin) {
        FileConfiguration config = plugin.getConfig();

        redisHost = config.getString("redis.host", "localhost");
        redisPort = config.getInt("redis.port", 6379);
        redisPassword = config.getString("redis.password", "");
        redisDatabase = config.getInt("redis.database", 0);
        redisMaxTotal = config.getInt("redis.pool.max-total", 10);
        redisMaxIdle = config.getInt("redis.pool.max-idle", 5);
        redisMinIdle = config.getInt("redis.pool.min-idle", 1);

        mongoUri = config.getString("mongodb.uri", "mongodb://localhost:27017");
        mongoDatabase = config.getString("mongodb.database", "apolo");
        mongoConnectionTimeout = config.getInt("mongodb.connection-timeout", 5000);
        mongoSocketTimeout = config.getInt("mongodb.socket-timeout", 10000);
        mongoMaxPoolSize = config.getInt("mongodb.max-pool-size", 10);

        tpaRequestTimeout = config.getInt("tpa.request-timeout", 30);
        tpaCooldown = config.getInt("tpa.cooldown", 10);

        repairCooldown = config.getInt("repair.cooldown", 60);

        flyRestoreOnJoin = config.getBoolean("fly.restore-on-join", true);
        godmodeRestoreOnJoin = config.getBoolean("godmode.restore-on-join", true);

        warpsPerPage = config.getInt("warps.per-page", 6);
        warpCooldown = config.getInt("warps.cooldown", 5);

        startingBalance = config.getDouble("economy.starting-balance", 500.0);
        currencySymbol = config.getString("economy.currency-symbol", "$");
        currencyNameSingular = config.getString("economy.currency-name-singular", "Dólar");
        currencyNamePlural = config.getString("economy.currency-name-plural", "Dólares");
        decimalDigits = config.getInt("economy.decimal-digits", 2);
        maxBalance = config.getDouble("economy.max-balance", 1000000000.0);
        minTransferAmount = config.getDouble("economy.min-transfer-amount", 1.0);

        balanceCacheTtl = config.getInt("cache.balance-ttl", 300);
        userCacheTtl = config.getInt("cache.user-ttl", 600);

        messagesFile = config.getString("messages-file", "messages.yml");
        serverId = config.getString("server-id", "server");
    }
}
