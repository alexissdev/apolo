package dev.apolo.api.event;

import dev.apolo.api.model.TpaRequestModel;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class ApoloTpaDenyEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player denier;
    private final TpaRequestModel request;

    public ApoloTpaDenyEvent(Player denier, TpaRequestModel request) {
        this.denier = denier;
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
