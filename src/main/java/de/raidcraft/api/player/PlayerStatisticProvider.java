package de.raidcraft.api.player;

import org.bukkit.entity.Player;

/**
 * @author mdoering
 */
public interface PlayerStatisticProvider {

    public int onJoin(Player player);

    public int onQuit(Player player);
}
