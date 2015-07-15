package de.raidcraft.api.conversations.events;

import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.event.HandlerList;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RCConversationAbortedEvent extends RCConversationEvent {

    private final ConversationEndReason reason;
    private boolean cancelled;

    public RCConversationAbortedEvent(Conversation conversation, ConversationEndReason reason) {

        super(conversation);
        this.reason = reason;
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
