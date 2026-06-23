package dev.apolo.listeners.impl;

import dev.apolo.api.event.ApoloUserCreateEvent;
import dev.apolo.api.event.ApoloUserLoadEvent;
import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.model.UserModel;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IMessageService;
import dev.apolo.api.service.IUserService;
import dev.apolo.core.repository.interfaces.IPlayerStateRepository;
import dev.apolo.core.usecase.RestorePlayerStateUseCase;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;

@RequiredArgsConstructor
public class PlayerSessionListener implements Listener {
    private final IUserService userService;
    private final IMessageService messageService;
    private final RestorePlayerStateUseCase restorePlayerStateUseCase;
    private final IPlayerStateRepository playerStateRepository;

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Registrar en el set global de jugadores online (cross-server)
        playerStateRepository.registerOnlinePlayer(
            player.getUniqueId().toString(), player.getName());

        ServiceResult<UserModel> result = userService.getOrCreateUser(player);

        if (result.isSuccess()) {
            result.getData().ifPresent(user -> {
                boolean isNew = user.getFirstJoin() == user.getLastSeen() || !player.hasPlayedBefore();

                if (isNew) {
                    messageService.sendMessage(player, MessageKey.USER_FIRST_JOIN,
                        Collections.singletonMap("player", player.getName()));
                    player.getServer().getPluginManager().callEvent(new ApoloUserCreateEvent(player, user));
                } else {
                    messageService.sendMessage(player, MessageKey.USER_WELCOME_BACK,
                        Collections.singletonMap("player", player.getName()));
                    player.getServer().getPluginManager().callEvent(new ApoloUserLoadEvent(player, user));
                }
            });
        }

        restorePlayerStateUseCase.execute(RestorePlayerStateUseCase.Input.builder().player(player).build());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        playerStateRepository.unregisterOnlinePlayer(uuid, player.getName());
        playerStateRepository.setSocialSpy(uuid, false);
        playerStateRepository.setCommandSpy(uuid, false);

        userService.syncState(player);
    }
}
