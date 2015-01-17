package de.raidcraft.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * @author Silthus
 */
public class RCPlayerGainExpEvent extends PlayerEvent {

    private static final HandlerList HANDLER = new HandlerList();

    private final int exp;

    public RCPlayerGainExpEvent(Player player, int exp) {

        super(player);
        this.exp = exp;
    }

    public static HandlerList getHandlerList() {

        return HANDLER;
    }

    public int getGainedExp() {

        return exp;
    }

    @Override
    public HandlerList getHandlers() {

        return HANDLER;
    }
}
