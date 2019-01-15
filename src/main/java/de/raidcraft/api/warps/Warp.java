package de.raidcraft.api.warps;

import org.bukkit.Location;

/**
 * Represents a warp
 *
 * @author xanily
 */
public interface Warp {

    /**
     * Gets the warp Location as {@link org.bukkit.Location]
     *
     * @return Location
     */
    Location getWarpLocation();

    /**
     * Gets the warp Location name as string
     *
     * @return String
     */
    String getWarpName();
}
