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
public class RCConversationChangedStageEvent extends RCConversationEvent {

    private final Optional<Stage> oldStage;
    private final Stage newStage;

    public RCConversationChangedStageEvent(Conversation<?> conversation, Optional<Stage> oldStage, Stage newStage) {

        super(conversation);
        this.oldStage = oldStage;
        this.newStage = newStage;
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
