package de.raidcraft.api.random;

import org.bukkit.entity.Player;

/**
 * Represents an object that is obtainable by a {@link org.bukkit.entity.Player}.
 */
public interface Obtainable {

    /**
     * Adds an instance of this object to the given player.
     * If the object implements {@link de.raidcraft.api.random.RDSObjectCreator} a new instance will be created.
     *
     * @param player to add object to
     */
    void addTo(Player player);
}
