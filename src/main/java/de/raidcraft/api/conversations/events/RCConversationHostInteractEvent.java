package de.raidcraft.api.conversations.events;

import de.raidcraft.api.conversations.host.ConversationHost;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Silthus
 */
@Getter
@Setter
public class RCConversationHostInteractEvent extends Event implements Cancellable {

    private final Player player;
    private ConversationHost host;
    private boolean cancelled;

    public RCConversationHostInteractEvent(Player player, ConversationHost host) {

        this.player = player;
        this.host = host;
    }

    //<-- Handler -->//

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }
}
