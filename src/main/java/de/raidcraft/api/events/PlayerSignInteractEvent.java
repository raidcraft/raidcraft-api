package de.raidcraft.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author Dragonfire
 */
public class PlayerSignInteractEvent extends Event {
    private PlayerInteractEvent parentEvent;

    public PlayerSignInteractEvent(PlayerInteractEvent parentEvent) {
        this.parentEvent = parentEvent;
    }

    public PlayerInteractEvent getParentEvent() {
        return parentEvent;
    }

    // Bukkit stuff
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }
}
