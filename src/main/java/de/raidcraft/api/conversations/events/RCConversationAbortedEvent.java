package de.raidcraft.api.conversations.events;

import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import de.raidcraft.api.conversations.host.ConversationHost;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Optional;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RCConversationAbortedEvent extends Event {

    private final Conversation<?> conversation;
    private final ConversationEndReason reason;
    private boolean cancelled;

    public RCConversationAbortedEvent(Conversation<?> conversation, ConversationEndReason reason) {

        this.conversation = conversation;
        this.reason = reason;
    }

    public Optional<Player> getPlayer() {

        if (getConversation().getEntity() instanceof Player) {
            return Optional.of((Player) getConversation().getEntity());
        }
        return Optional.empty();
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
