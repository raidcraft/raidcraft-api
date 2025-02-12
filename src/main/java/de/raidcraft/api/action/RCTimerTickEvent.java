package de.raidcraft.api.action;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.event.HandlerList;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RCTimerTickEvent extends RCTimerEvent {

    public RCTimerTickEvent(Timer timer) {

        super(timer);
    }

    private static final HandlerList HANDLER = new HandlerList();

    public static HandlerList getHandlerList() {

        return HANDLER;
    }

    @Override
    public HandlerList getHandlers() {

        return HANDLER;
    }
}
