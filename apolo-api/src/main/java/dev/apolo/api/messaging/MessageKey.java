package dev.apolo.api.messaging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageKey {

    // Prefix
    PREFIX("prefix"),

    // General
    NO_PERMISSION("general.no-permission"),
    PLAYER_NOT_FOUND("general.player-not-found"),
    ONLY_PLAYERS("general.only-players"),
    PLAYER_OFFLINE("general.player-offline"),
    INVALID_ARGUMENTS("general.invalid-arguments"),
    CANNOT_TARGET_SELF("general.cannot-target-self"),

    // GameMode
    GAMEMODE_CHANGED_SELF("gamemode.changed-self"),
    GAMEMODE_CHANGED_OTHER_SENDER("gamemode.changed-other-sender"),
    GAMEMODE_CHANGED_OTHER_TARGET("gamemode.changed-other-target"),
    GAMEMODE_NO_PERMISSION_MODE("gamemode.no-permission-mode"),
    GAMEMODE_INVALID("gamemode.invalid"),
    GAMEMODE_USAGE("gamemode.usage"),

    // TPA
    TPA_REQUEST_SENT("tpa.request-sent"),
    TPA_REQUEST_RECEIVED("tpa.request-received"),
    TPA_ALREADY_PENDING("tpa.already-pending"),
    TPA_COOLDOWN("tpa.cooldown"),
    TPA_ACCEPTED_SENDER("tpa.accepted-sender"),
    TPA_ACCEPTED_TARGET("tpa.accepted-target"),
    TPA_DENIED_SENDER("tpa.denied-sender"),
    TPA_DENIED_TARGET("tpa.denied-target"),
    TPA_EXPIRED_SENDER("tpa.expired-sender"),
    TPA_EXPIRED_TARGET("tpa.expired-target"),
    TPA_NO_PENDING("tpa.no-pending"),
    TPA_TELEPORTING("tpa.teleporting"),
    TPA_USAGE("tpa.usage"),
    TPA_NOTHING_TO_CANCEL("tpa.nothing-to-cancel"),
    TPA_CANCELLED_SENDER("tpa.cancelled-sender"),
    TPA_CANCELLED_TARGET("tpa.cancelled-target"),

    // TPHere
    TPHERE_REQUEST_SENT("tphere.request-sent"),
    TPHERE_REQUEST_RECEIVED("tphere.request-received"),
    TPHERE_ACCEPTED_SENDER("tphere.accepted-sender"),
    TPHERE_ACCEPTED_TARGET("tphere.accepted-target"),
    TPHERE_USAGE("tphere.usage"),

    // Warp
    WARP_CREATED("warp.created"),
    WARP_DELETED("warp.deleted"),
    WARP_NOT_FOUND("warp.not-found"),
    WARP_ALREADY_EXISTS("warp.already-exists"),
    WARP_TELEPORTING("warp.teleporting"),
    WARP_COOLDOWN("warp.cooldown"),
    WARP_LIST_HEADER("warp.list.header"),
    WARP_LIST_ENTRY("warp.list.entry"),
    WARP_LIST_FOOTER("warp.list.footer"),
    WARP_LIST_EMPTY("warp.list.empty"),
    WARP_SETWARP_USAGE("warp.setwarp-usage"),
    WARP_DELWARP_USAGE("warp.delwarp-usage"),
    WARP_USAGE("warp.usage"),

    // Fly
    FLY_ENABLED_SELF("fly.enabled-self"),
    FLY_DISABLED_SELF("fly.disabled-self"),
    FLY_ENABLED_OTHER_SENDER("fly.enabled-other-sender"),
    FLY_DISABLED_OTHER_SENDER("fly.disabled-other-sender"),
    FLY_ENABLED_OTHER_TARGET("fly.enabled-other-target"),
    FLY_DISABLED_OTHER_TARGET("fly.disabled-other-target"),
    FLY_USAGE("fly.usage"),

    // GodMode
    GOD_ENABLED_SELF("godmode.enabled-self"),
    GOD_DISABLED_SELF("godmode.disabled-self"),
    GOD_ENABLED_OTHER_SENDER("godmode.enabled-other-sender"),
    GOD_DISABLED_OTHER_SENDER("godmode.disabled-other-sender"),
    GOD_ENABLED_OTHER_TARGET("godmode.enabled-other-target"),
    GOD_DISABLED_OTHER_TARGET("godmode.disabled-other-target"),
    GOD_USAGE("godmode.usage"),

    // Repair
    REPAIR_SUCCESS_HAND("repair.success-hand"),
    REPAIR_SUCCESS_ARMOR("repair.success-armor"),
    REPAIR_SUCCESS_ALL("repair.success-all"),
    REPAIR_NOTHING_TO_REPAIR("repair.nothing-to-repair"),
    REPAIR_COOLDOWN("repair.cooldown"),
    REPAIR_USAGE("repair.usage"),

    // Economy
    ECONOMY_BALANCE_SELF("economy.balance-self"),
    ECONOMY_BALANCE_OTHER("economy.balance-other"),
    ECONOMY_DEPOSIT_SUCCESS("economy.deposit-success"),
    ECONOMY_WITHDRAW_SUCCESS("economy.withdraw-success"),
    ECONOMY_TRANSFER_SENT("economy.transfer-sent"),
    ECONOMY_TRANSFER_RECEIVED("economy.transfer-received"),
    ECONOMY_INSUFFICIENT_FUNDS("economy.insufficient-funds"),
    ECONOMY_INVALID_AMOUNT("economy.invalid-amount"),
    ECONOMY_MIN_TRANSFER_AMOUNT("economy.min-transfer-amount"),
    ECONOMY_ACCOUNT_NOT_FOUND("economy.account-not-found"),
    ECONOMY_ACCOUNT_CREATED("economy.account-created"),
    ECONOMY_TOP_HEADER("economy.top-header"),
    ECONOMY_TOP_ENTRY("economy.top-entry"),
    ECONOMY_TOP_FOOTER("economy.top-footer"),
    ECONOMY_SET_BALANCE_SUCCESS("economy.set-balance-success"),
    ECONOMY_ADMIN_DEPOSIT_SUCCESS("economy.admin-deposit-success"),
    ECONOMY_ADMIN_WITHDRAW_SUCCESS("economy.admin-withdraw-success"),
    ECONOMY_TRANSFER_USAGE("economy.transfer-usage"),
    ECONOMY_BALANCE_USAGE("economy.balance-usage"),

    // User
    USER_FIRST_JOIN("user.first-join"),
    USER_WELCOME_BACK("user.welcome-back"),

    // Private messages
    MSG_SENT("msg.sent"),
    MSG_RECEIVED("msg.received"),
    MSG_PLAYER_NOT_ONLINE("msg.player-not-online"),
    MSG_USAGE("msg.usage"),
    REPLY_USAGE("reply.usage"),
    REPLY_NO_ONE("reply.no-one"),

    // SocialSpy
    SOCIALSPY_ENABLED("socialspy.enabled"),
    SOCIALSPY_DISABLED("socialspy.disabled"),
    SOCIALSPY_FORMAT("socialspy.format"),

    // CommandSpy
    COMMANDSPY_ENABLED("commandspy.enabled"),
    COMMANDSPY_DISABLED("commandspy.disabled"),
    COMMANDSPY_FORMAT("commandspy.format");

    private final String path;
}
