package de.raidcraft.api.quests;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event informs other plugins that the QuestPlugin successfully
 * finished loading all registered {@link de.raidcraft.api.config.ConfigLoader}.
 */
public class RCQuestConfigsLoaded extends Event {


    //<-- Handler -->//
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }
}
