package de.raidcraft.api.random;

import org.bukkit.Location;

/**
 * Represents a spawnable object that can be placed anywhere in the world.
 * This could be entities, items, lootchests, etc.
 */
public interface Spawnable {

    /**
     * Spawns the object at the given location.
     * Will create a new instance of the object if it represents a template or clonable object.
     *
     * @param location to spawn object at
     */
    void spawn(Location location);
}
