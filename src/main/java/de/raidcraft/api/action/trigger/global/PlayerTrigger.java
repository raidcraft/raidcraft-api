package de.raidcraft.api.action.trigger.global;

import de.raidcraft.api.action.trigger.Trigger;
import de.raidcraft.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Silthus
 */
public class PlayerTrigger extends Trigger implements Listener {

    private Map<UUID, Location> playerLocations = new HashMap<>();

    public PlayerTrigger() {

        super("player", "interact", "block.break", "block.place", "move");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {

        informListeners("interact", event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {

        informListeners("block.break", event.getPlayer(), config -> {
            Material block = Material.getMaterial(config.getString("block", "minecraft:air"));
            return block == Material.AIR || block == event.getBlock().getType();
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {

        informListeners("block.place", event.getPlayer(), config -> {
            Material block = Material.getMaterial(config.getString("block", "minecraft:air"));
            return block == Material.AIR || block == event.getBlock().getType();
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent event) {

        if (!hasMoved(event.getPlayer(), event.getTo())) return;

        // if no coordinates are defined always return true
        // otherwise check the coordinates and the radius
        informListeners("move", event.getPlayer(), config -> {
            
            World world = Bukkit.getWorld(config.getString("world"));
            if (config.isSet("world") && (world == null || !event.getPlayer().getWorld().equals(world))) return false;
            if (world == null) world = event.getPlayer().getWorld();
            return ((!config.isSet("x") || !config.isSet("y") || !config.isSet("z"))
                        || LocationUtil.isWithinRadius(
                        event.getPlayer().getLocation(),
                        new Location(
                                world,
                                config.getInt("x"),
                                config.getInt("y"),
                                config.getInt("z")),
                        config.getInt("radius", 0)));
        });
    }

    private boolean hasMoved(Player player, Location to) {

        if (!playerLocations.containsKey(player.getUniqueId())) {
            playerLocations.put(player.getUniqueId(), player.getLocation());
        }
        Location current = playerLocations.get(player.getUniqueId());
        if (current.getBlockX() != to.getBlockX() || current.getBlockY() != to.getBlockY() || current.getBlockZ() != to.getBlockZ()) {
            playerLocations.put(player.getUniqueId(), to);
            return true;
        }
        return false;
    }
}