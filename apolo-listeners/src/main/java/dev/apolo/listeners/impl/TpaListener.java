package dev.apolo.listeners.impl;

import dev.apolo.api.event.ApoloTpaExpiredEvent;
import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.model.TpaRequestModel;
import dev.apolo.api.service.IMessageService;
import dev.apolo.api.service.ITpaService;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class TpaListener implements Listener {
    private final ITpaService tpaService;
    private final IMessageService messageService;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Optional<TpaRequestModel> pending = tpaService.getPendingRequest(player);
        if (pending.isPresent()) {
            TpaRequestModel request = pending.get();
            tpaService.deny(player);
            Player sender = player.getServer().getPlayer(UUID.fromString(request.getFromUuid()));
            if (sender != null) {
                messageService.sendMessage(sender, MessageKey.TPA_EXPIRED_SENDER,
                    Collections.singletonMap("player", player.getName()));
            }
            ApoloTpaExpiredEvent expiredEvent = new ApoloTpaExpiredEvent(request);
            player.getServer().getPluginManager().callEvent(expiredEvent);
        }
    }
}
