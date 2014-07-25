package de.raidcraft.api.conversations;

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
public class RCPlayerStartConversationEvent extends Event implements Cancellable {

    private final Player player;
    private ConversationHost host;
    private String conversation;
    private boolean cancelled;

    public RCPlayerStartConversationEvent(Player player, ConversationHost host, String conversation) {

        this.player = player;
        this.host = host;
        this.conversation = conversation;
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
