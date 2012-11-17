package de.raidcraft;

import de.raidcraft.api.Component;
import de.raidcraft.api.bukkit.BukkitPlayer;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.player.PlayerComponent;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.api.player.UnknownPlayerException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This is the static class that gives access to all important API
 * methods and components.
 *
 * @author Silthus
 */
public class RaidCraft implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {

        players.remove(event.getPlayer().getName());
    }

    /*///////////////////////////////////////////////////////////////////////////
     //                      RaidCraft RCRPG Utility Class
     ///////////////////////////////////////////////////////////////////////////*/

    public static final Logger LOGGER = Logger.getLogger("Minecraft.RaidCraft");

    private static final Map<String, RCPlayer> players = new HashMap<>();
    private static final Map<Class<? extends Component>, Component> components = new HashMap<>();

    /**
     * Gets the wrapped Player for interaction with the player and his surroundings.
     *
     * @param name of the player
     *
     * @return RCPlayer
     */
    public static RCPlayer safeGetPlayer(String name) throws UnknownPlayerException {

        Player player = Bukkit.getPlayer(name);
        RCPlayer rcPlayer = null;

        if (player == null) {
            try {
                // player is not online so we need to check our database
                // if no match is found it will throw an exception
                ResultSet resultSet = Database.getConnection().prepareStatement(
                        "SELECT count(*) as count, player FROM `raidcraft_guests` WHERE player IS LIKE '%" + name + "%'").executeQuery();
                if (resultSet.next()) {
                    if (resultSet.getInt("count") == 1) {
                        name = resultSet.getString("player");
                        // add the player to cache if he isnt added
                        if (players.containsKey(name)) {
                            rcPlayer = players.get(name);
                        } else {
                            rcPlayer = new BukkitPlayer(name);
                            players.put(name, rcPlayer);
                        }
                    }
                }
            } catch (SQLException e) {
                LOGGER.warning(e.getMessage());
                e.printStackTrace();
            }
        } else {
            rcPlayer = getPlayer(player);
        }
        if (rcPlayer == null) throw new UnknownPlayerException("Es gibt keinen Spieler mit dem Namen: " + name);
        return rcPlayer;
    }

    /**
     * Gets the player but will ignore the exception thrown.
     * This can be used when you know that the player name is correct.
     *
     * @param name of the player
     *
     * @return wrapped RCPlayer object
     */
    public static RCPlayer getPlayer(String name) {

        try {
            return safeGetPlayer(name);
        } catch (UnknownPlayerException e) {
            LOGGER.severe(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the player from the Bukkit Player object.
     *
     * @param player object from bukkit
     *
     * @return RCPlayer
     */
    public static RCPlayer getPlayer(Player player) {

        RCPlayer rcPlayer;
        if (players.containsKey(player.getName())) {
            rcPlayer = players.get(player.getName());
        } else {
            rcPlayer = new BukkitPlayer(player);
            players.put(player.getName(), rcPlayer);
        }
        return rcPlayer;
    }

    /**
     * Gets the given PlayerComponent for the given Player object.
     *
     * @param player to get component for
     * @param clazz  of the component
     * @param <T>    component type
     *
     * @return PlayerComponent
     */
    public static <T extends PlayerComponent> T getPlayerComponent(RCPlayer player, Class<T> clazz) {

        return player.getComponent(clazz);
    }

    /**
     * Registers the given component with the RaidCraft API making it usable.
     *
     * @param clazz     to register
     * @param component instance
     */
    public static void registerComponent(Class<? extends Component> clazz, Component component) {

        components.put(clazz, component);
    }

    /**
     * Unregisters the given component class.
     *
     * @param clazz to unregister
     */
    public static void unregisterComponent(Class<? extends Component> clazz) {

        components.remove(clazz);
    }

    /**
     * Gets the given component instance.
     *
     * @param clazz of the component
     * @param <T>   type
     *
     * @return casted component instance
     */
    public static <T extends Component> T getComponent(Class<T> clazz) {

        return clazz.cast(components.get(clazz));
    }

    public static Database getDatabase() {

        return Database.getInstance();
    }
}
