package de.raidcraft.api.conversations;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Philip
 */
public interface ConversationHost {

    String getName();

    Location getLocation();

    String getUniqueId();

    String getDefaultConversationName();

    void setConversation(Player player, String conversation);

    String getConversation(Player player);
}
