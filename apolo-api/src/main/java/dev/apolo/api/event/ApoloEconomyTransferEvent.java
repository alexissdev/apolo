package dev.apolo.api.event;

import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class ApoloEconomyTransferEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;

    private final String fromUuid;
    private final String toUuid;
    private final double amount;
    private final String reason;

    public ApoloEconomyTransferEvent(String fromUuid, String toUuid, double amount, String reason) {
        this.fromUuid = fromUuid;
        this.toUuid = toUuid;
        this.amount = amount;
        this.reason = reason;
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
