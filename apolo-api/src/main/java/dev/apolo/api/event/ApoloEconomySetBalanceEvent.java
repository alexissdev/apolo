package dev.apolo.api.event;

import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class ApoloEconomySetBalanceEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;

    private final String uuid;
    private final double oldBalance;
    private final double newBalance;

    public ApoloEconomySetBalanceEvent(String uuid, double oldBalance, double newBalance) {
        this.uuid = uuid;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
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
