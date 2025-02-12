package de.raidcraft.api.conversations.events;

import de.raidcraft.api.conversations.host.ConversationHost;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
public class ConversationHostInteractEvent extends Event implements Cancellable {

    @Getter
    private final String hostIdentifier;
    @Getter
    private final ConversationHost<?> host;
    @Getter
    private final Player player;
    @Getter
    @Setter
    private boolean cancelled;

    // Bukkit stuff
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }
}
