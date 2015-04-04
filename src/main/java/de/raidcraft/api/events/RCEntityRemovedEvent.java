package de.raidcraft.api.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Silthus
 */

public class RCEntityRemovedEvent extends Event {

    private final Entity entity;

    public RCEntityRemovedEvent(Entity entity) {

        this.entity = entity;
    }

    public Entity getEntity() {

        return entity;
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
