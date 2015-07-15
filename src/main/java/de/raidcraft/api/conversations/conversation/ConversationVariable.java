package de.raidcraft.api.conversations.conversation;

import de.raidcraft.api.conversations.Conversations;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @author mdoering
 */
@FunctionalInterface
public interface ConversationVariable {

    static Optional<String> of(Player player, String key) {

        return Optional.ofNullable(Conversations.getActiveConversation(player).get().getString(key));
    }

    String replace(Conversation<Player> conversation);
}
