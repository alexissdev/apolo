package dev.apolo.listeners.impl;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.model.CommandSpyModel;
import dev.apolo.api.service.IMessageService;
import dev.apolo.core.repository.interfaces.IPlayerStateRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class CommandSpyListener implements Listener {
    private final IPlayerStateRepository playerStateRepository;
    private final IMessageService messageService;
    private final Consumer<CommandSpyModel> crossServerPublisher;
    private final String serverId;

    public CommandSpyListener(IPlayerStateRepository playerStateRepository,
                              IMessageService messageService,
                              Consumer<CommandSpyModel> crossServerPublisher,
                              String serverId) {
        this.playerStateRepository = playerStateRepository;
        this.messageService = messageService;
        this.crossServerPublisher = crossServerPublisher;
        this.serverId = serverId;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();

        CommandSpyModel model = CommandSpyModel.builder()
            .playerUuid(player.getUniqueId().toString())
            .playerName(player.getName())
            .command(command)
            .serverId(serverId)
            .timestamp(System.currentTimeMillis())
            .build();

        // Entregar a staff con commandspy en ESTE servidor
        deliverLocally(model);

        // Publicar cross-server para los otros servidores
        crossServerPublisher.accept(model);
    }

    public void deliverLocally(CommandSpyModel model) {
        Set<String> spyPlayers = playerStateRepository.getCommandSpyPlayers();
        if (spyPlayers.isEmpty()) return;

        Map<String, String> placeholders = Map.of(
            "server", model.getServerId(),
            "player", model.getPlayerName(),
            "command", model.getCommand()
        );

        for (String spyUuid : spyPlayers) {
            if (spyUuid.equals(model.getPlayerUuid())) continue;
            try {
                Player spy = Bukkit.getPlayer(UUID.fromString(spyUuid));
                if (spy != null && spy.isOnline()) {
                    messageService.sendMessage(spy, MessageKey.COMMANDSPY_FORMAT, placeholders);
                }
            } catch (IllegalArgumentException ignored) {}
        }
    }
}
