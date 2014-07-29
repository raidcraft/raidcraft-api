package de.raidcraft.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author Dragonfire
 */
public class PlayerSignInteractEvent extends Event {

    private static final HandlerList HANDLER = new HandlerList();

    private PlayerInteractEvent parentEvent;

    public PlayerSignInteractEvent(PlayerInteractEvent parentEvent) {

        this.parentEvent = parentEvent;
    }

    public static HandlerList getHandlerList() {

        return HANDLER;
    }

    public PlayerInteractEvent getParentEvent() {

        return parentEvent;
    }

    @Override
    public HandlerList getHandlers() {

        return HANDLER;
    }
}
