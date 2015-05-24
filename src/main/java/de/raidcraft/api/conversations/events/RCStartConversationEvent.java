package de.raidcraft.api.conversations.events;

import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.host.ConversationHost;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Optional;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RCStartConversationEvent extends Event implements Cancellable {

    private final Conversation<?> conversation;
    private boolean cancelled;

    public RCStartConversationEvent(Conversation<?> conversation) {

        this.conversation = conversation;
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
