package de.raidcraft.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dragonfire
 */
public class PlayerUtil {

    public static List<Player> getPlayerNearby(Location location, int radius) {

        return location.getWorld().getPlayers()
                .stream()
                .filter(player -> LocationUtil.isWithinRadius(location, player.getLocation(), radius))
                .collect(Collectors.toList());
    }

    public static List<Player> getPlayerNearby(Player player, int radius) {

        return getPlayerNearby(player.getLocation(), radius);
    }

    public static void broadcastMessage(Player player, int radius, String message) {

        getPlayerNearby(player, radius).forEach(tmpPlayer -> tmpPlayer.sendMessage(message));
    }
}
