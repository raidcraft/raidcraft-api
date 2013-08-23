package de.raidcraft.api.conversations;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Philip Urban
 */
public interface ConversationProvider {

    public void registerConversation(ConfigurationSection configuration, String name);
}
