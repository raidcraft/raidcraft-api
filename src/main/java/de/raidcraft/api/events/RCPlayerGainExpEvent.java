package de.raidcraft.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Silthus
 */
public class RCPlayerGainExpEvent extends Event {

    private final Player player;
    private final int exp;

    public RCPlayerGainExpEvent(Player player, int exp) {

        this.player = player;
        this.exp = exp;
    }

    public Player getPlayer() {

        return player;
    }

    public int getGainedExp() {

        return exp;
    }

    /*///////////////////////////////////////////////////
    //              Needed Bukkit Stuff
    ///////////////////////////////////////////////////*/

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }
}
