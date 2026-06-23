package dev.apolo.api.event;

import dev.apolo.api.model.PrivateMessageModel;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired before a private message is sent cross-server via /msg.
 * Cancelling this event prevents delivery.
 */
@Getter
public class ApoloPrivateMessageEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;

    private final Player sender;
    private final PrivateMessageModel message;

    public ApoloPrivateMessageEvent(Player sender, PrivateMessageModel message) {
        this.sender = sender;
        this.message = message;
    }

    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
    @Override public boolean isCancelled() { return cancelled; }
    @Override public void setCancelled(boolean cancel) { this.cancelled = cancel; }
}
