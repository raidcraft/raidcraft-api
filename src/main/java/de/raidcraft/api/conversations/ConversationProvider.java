package de.raidcraft.api.conversations;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Philip Urban
 */
public interface ConversationProvider {

    public void registerConversation(ConfigurationSection configuration, String name);

    public void triggerConversation(Player player, String conversationName, ConversationHost conversationHost);
}
