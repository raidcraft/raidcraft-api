package de.raidcraft.api.conversations.events;

import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.stage.Stage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.event.HandlerList;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RCConversationStageTriggeredEvent extends RCConversationEvent {

    private final Stage stage;

    public RCConversationStageTriggeredEvent(Conversation conversation, Stage stage) {

        super(conversation);
        this.stage = stage;
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
