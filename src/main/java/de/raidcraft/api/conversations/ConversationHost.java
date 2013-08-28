package de.raidcraft.api.conversations;

import org.bukkit.Location;

/**
 * @author Philip
 */
public interface ConversationHost {

    public String getName();

    public Location getLocation();

    public String getUniqueId();
}
