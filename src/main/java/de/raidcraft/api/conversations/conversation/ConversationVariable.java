package de.raidcraft.api.conversations.conversation;

import org.bukkit.entity.Player;

/**
 * @author mdoering
 */
@FunctionalInterface
public interface ConversationVariable {

    String replace(Conversation<Player> conversation);
}
