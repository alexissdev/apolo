package dev.apolo.api.event;

import dev.apolo.api.model.UserModel;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class ApoloUserCreateEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final UserModel user;

    public ApoloUserCreateEvent(Player player, UserModel user) {
        this.player = player;
        this.user = user;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
