package dev.apolo.redis;

public final class RedisKeys {
    private RedisKeys() {}

    private static final String PREFIX = "apolo";

    public static String tpaPending(String targetUuid) {
        return PREFIX + ":tpa:pending:" + targetUuid;
    }

    public static String tpaCooldown(String uuid) {
        return PREFIX + ":tpa:cooldown:" + uuid;
    }

    public static String warp(String name) {
        return PREFIX + ":warp:" + name.toLowerCase();
    }

    public static String warpList() {
        return PREFIX + ":warps:list";
    }

    public static String flyState(String uuid) {
        return PREFIX + ":fly:" + uuid;
    }

    public static String godState(String uuid) {
        return PREFIX + ":god:" + uuid;
    }

    public static String repairCooldown(String uuid) {
        return PREFIX + ":repair:cooldown:" + uuid;
    }

    public static String economyBalance(String uuid) {
        return PREFIX + ":economy:balance:" + uuid;
    }

    public static String economyUser(String uuid) {
        return PREFIX + ":economy:user:" + uuid;
    }

    public static String onlinePlayerName(String uuid) {
        return PREFIX + ":player:name:" + uuid;
    }

    public static String onlinePlayerUuid(String name) {
        return PREFIX + ":player:uuid:" + name.toLowerCase();
    }

    public static String msgLastSender(String uuid) {
        return PREFIX + ":msg:last-sender:" + uuid;
    }

    public static final String ONLINE_PLAYERS_SET = PREFIX + ":online:players";
    public static final String ONLINE_NAMES_SET = PREFIX + ":online:names";
    public static final String SOCIALSPY_PLAYERS_SET = PREFIX + ":socialspy:players";
    public static final String COMMANDSPY_PLAYERS_SET = PREFIX + ":commandspy:players";

    public static final String CHANNEL_WARP_UPDATE = PREFIX + ":events:warp-update";
    public static final String CHANNEL_PLAYER_STATE = PREFIX + ":events:player-state";
    public static final String CHANNEL_ECONOMY_UPDATE = PREFIX + ":events:economy-update";
    public static final String CHANNEL_PRIVATE_MESSAGE = PREFIX + ":events:private-message";
    public static final String CHANNEL_COMMAND_SPY = PREFIX + ":events:command-spy";
}
