package de.raidcraft.api.flight.flight;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author mdoering
 */
@Getter
@Setter
public class RCStartFlightEvent extends Event implements Cancellable {

    private final Player player;
    private final Flight flight;
    private boolean cancelled;
    private String message;

    public RCStartFlightEvent(Player player, Flight flight) {

        this.player = player;
        this.flight = flight;
    }

    /*///////////////////////////////////////////////////
    //              Needed Bukkit Stuff
    ///////////////////////////////////////////////////*/

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {

        return handlers;
    }

    public HandlerList getHandlers() {

        return handlers;
    }
}
