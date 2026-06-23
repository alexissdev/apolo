package dev.apolo.api.event;

import dev.apolo.api.model.TpaRequestModel;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class ApoloTpaExpiredEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final TpaRequestModel request;

    public ApoloTpaExpiredEvent(TpaRequestModel request) {
        this.request = request;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
