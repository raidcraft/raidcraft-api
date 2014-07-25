package de.raidcraft.api.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 */
public class RCEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public RCEvent() {

        super(false);
    }

    public RCEvent(final boolean isAsync) {

        super(isAsync);
    }

    public void callEvent() {

        Bukkit.getServer().getPluginManager().callEvent(this);
    }

    @Override
    public HandlerList getHandlers() {

        return HANDLERS;
    }

    public static HandlerList getHandlerList() {

        return HANDLERS;
    }
}