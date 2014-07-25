package de.raidcraft.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author Dragonfire
 */
public class PlayerSignInteractEvent extends RCEvent {

    private PlayerInteractEvent parentEvent;

    public PlayerSignInteractEvent(PlayerInteractEvent parentEvent) {

        this.parentEvent = parentEvent;
    }

    public PlayerInteractEvent getParentEvent() {

        return parentEvent;
    }
}
