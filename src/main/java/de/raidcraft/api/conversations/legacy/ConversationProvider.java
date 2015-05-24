package de.raidcraft.api.conversations.legacy;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Philip Urban
 */
public interface ConversationProvider {

    void registerConversation(ConfigurationSection configuration, String name);

    void triggerConversation(Player player, ConversationHost conversationHost);
}
