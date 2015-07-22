package de.raidcraft.api.conversations.host;

import org.bukkit.Location;

/**
 * @author mdoering
 */
public interface ConversationHostFactory<T> {

    /**
     * Creates a new {@link ConversationHost} at the given location.
     *
     * @return created conversation host
     */
    ConversationHost<T> create(String identifier, Location location);

    /**
     * Wraps the given host type into a {@link ConversationHost}.
     *
     * @param type to wrap
     * @return wrapped host
     */
    ConversationHost<T> create(T type);
}
