package de.raidcraft.api.conversations.events;

import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.host.ConversationHost;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class RCConversationEvent extends Event {

    private final Conversation conversation;

    public RCConversationEvent(Conversation conversation) {

        this.conversation = conversation;
    }

    public Player getPlayer() {

        return getConversation().getOwner();
    }

    public ConversationHost getHost() {

        return getConversation().getHost();
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
