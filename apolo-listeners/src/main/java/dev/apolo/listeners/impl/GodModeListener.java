package dev.apolo.listeners.impl;

import dev.apolo.api.service.IGodModeService;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

@RequiredArgsConstructor
public class GodModeListener implements Listener {
    private final IGodModeService godModeService;

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (godModeService.hasGodMode(player)) {
            event.setCancelled(true);
        }
    }
}
