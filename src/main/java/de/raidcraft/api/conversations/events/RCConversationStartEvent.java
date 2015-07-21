package de.raidcraft.api.conversations.events;

import de.raidcraft.api.conversations.conversation.Conversation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RCConversationStartEvent extends RCConversationEvent implements Cancellable {

    private boolean cancelled;

    public RCConversationStartEvent(Conversation conversation) {

        super(conversation);
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
