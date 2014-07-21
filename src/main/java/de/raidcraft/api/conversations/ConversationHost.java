package de.raidcraft.api.conversations;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Philip
 */
public interface ConversationHost {

    public String getName();

    public Location getLocation();

    public String getUniqueId();

    public String getDefaultConversationName();

    public void setConversation(Player player, String conversation);

    public String getConversation(Player player);

    public void interact(Player player);
}
