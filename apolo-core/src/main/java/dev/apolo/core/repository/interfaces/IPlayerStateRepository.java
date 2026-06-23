package dev.apolo.core.repository.interfaces;

import java.util.Optional;
import java.util.Set;

public interface IPlayerStateRepository {
    // Online player tracking (cross-server)
    void registerOnlinePlayer(String uuid, String name);
    void unregisterOnlinePlayer(String uuid, String name);
    Set<String> getOnlinePlayerNames();
    Optional<String> getPlayerUuidByName(String name);
    Optional<String> getPlayerNameByUuid(String uuid);

    // Private message last-sender
    void setLastMessageSender(String targetUuid, String senderUuid);
    Optional<String> getLastMessageSender(String uuid);

    // SocialSpy
    void setSocialSpy(String uuid, boolean enabled);
    boolean isSocialSpy(String uuid);
    Set<String> getSocialSpyPlayers();

    // CommandSpy
    void setCommandSpy(String uuid, boolean enabled);
    boolean isCommandSpy(String uuid);
    Set<String> getCommandSpyPlayers();
    void setFlyState(String uuid, boolean enabled);
    boolean getFlyState(String uuid);
    boolean hasFlyState(String uuid);
    void deleteFlyState(String uuid);

    void setGodState(String uuid, boolean enabled);
    boolean getGodState(String uuid);
    boolean hasGodState(String uuid);
    void deleteGodState(String uuid);

    void setRepairCooldown(String uuid, int ttlSeconds);
    boolean hasRepairCooldown(String uuid);
    long getRepairCooldownTtl(String uuid);

    void setTpaCooldown(String uuid, int ttlSeconds);
    boolean hasTpaCooldown(String uuid);
    long getTpaCooldownTtl(String uuid);

    void setTpaRequest(String targetUuid, String json, int ttlSeconds);
    String getTpaRequest(String targetUuid);
    boolean hasTpaRequest(String targetUuid);
    void deleteTpaRequest(String targetUuid);

    void setCachedBalance(String uuid, double balance, int ttlSeconds);
    Optional<Double> getCachedBalance(String uuid);
    void deleteCachedBalance(String uuid);
}
