package de.raidcraft.api.player;

import org.bukkit.entity.Player;

/**
 * Tracks player statistics on logon and logoff. Always return the full
 * value of the tracked statistic and do not calculate any difference.
 *
 * You need to register the statistic provider with
 * {@link de.raidcraft.RaidCraft#registerPlayerStatisticProvider(de.raidcraft.api.BasePlugin, String, PlayerStatisticProvider)}
 */
public interface PlayerStatisticProvider {

    /**
     * Gets the statistic value for the given player.
     * Value should be absolut and not calculated or dynamic per session.
     * The value should be the same between the logoff of the player and when he loggs on again.
     *
     * @param player to get value for
     * @return value of the statistic for the player
     */
    public int getStatisticValue(Player player);
}
