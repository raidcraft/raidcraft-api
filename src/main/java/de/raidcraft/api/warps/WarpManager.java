package de.raidcraft.api.warps;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.RaidCraftException;

/**
 * Handle warps
 */
public interface WarpManager {
    /**
     * Registers a warp
     *
     * @param warp
     * @param plugin
     */
    void registerWarp(Warp warp, BasePlugin plugin);

    /**
     * Unregisters a warp
     *
     * @param warp
     * @param plugin
     */
    void unregisterWarp(Warp warp, BasePlugin plugin);
}
