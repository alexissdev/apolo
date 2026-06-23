package dev.apolo.api.service;

import dev.apolo.api.model.PrivateMessageModel;
import dev.apolo.api.result.ServiceResult;
import org.bukkit.entity.Player;

import java.util.Set;

public interface IPrivateMessageService {
    ServiceResult<Void> sendMessage(Player from, String targetName, String message);
    ServiceResult<Void> reply(Player from, String message);
    void deliverLocally(PrivateMessageModel message);
    Set<String> getOnlinePlayerNames();
}
