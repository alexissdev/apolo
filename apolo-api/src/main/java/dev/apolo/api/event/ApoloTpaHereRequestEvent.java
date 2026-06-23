package dev.apolo.api.event;

import dev.apolo.api.model.TpaRequestModel;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class ApoloTpaHereRequestEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;

    private final Player from;
    private final Player target;
    private final TpaRequestModel request;

    public ApoloTpaHereRequestEvent(Player from, Player target, TpaRequestModel request) {
        this.from = from;
        this.target = target;
        this.request = request;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
