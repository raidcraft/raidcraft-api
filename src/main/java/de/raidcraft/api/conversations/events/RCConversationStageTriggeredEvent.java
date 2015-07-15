package de.raidcraft.api.conversations.events;

import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.api.conversations.stage.Stage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.Optional;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RCConversationStageTriggeredEvent extends RCConversationEvent {

    private final Stage stage;

    public RCConversationStageTriggeredEvent(Conversation<?> conversation, Stage stage) {

        super(conversation);
        this.stage = stage;
    }

    public Optional<Player> getPlayer() {

        if (getConversation().getOwner() instanceof Player) {
            return Optional.of((Player) getConversation().getOwner());
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
