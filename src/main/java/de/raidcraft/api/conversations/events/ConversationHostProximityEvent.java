package de.raidcraft.api.conversations.events;

import de.raidcraft.api.conversations.host.ConversationHost;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
public class ConversationHostProximityEvent extends Event {

    @Getter
    private final String hostIdentifier;
    @Getter
    private final ConversationHost<?> questHost;
    @Getter
    private final int radius;
    @Getter
    private final Player player;

    // Bukkit stuff
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

}
