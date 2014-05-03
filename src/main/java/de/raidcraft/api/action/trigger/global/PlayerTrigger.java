package de.raidcraft.api.action.trigger.global;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.trigger.Trigger;
import de.raidcraft.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * @author Silthus
 */
public class PlayerTrigger extends Trigger implements Listener {

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

        if (!RaidCraft.hasMoved(event.getPlayer(), event.getTo())) return;

        // if no coordinates are defined always return true
        // otherwise check the coordinates and the radius
        informListeners("move", event.getPlayer(), config ->
                (!config.isSet("x") || !config.isSet("y") || !config.isSet("z"))
                        || LocationUtil.isWithinRadius(
                        event.getPlayer().getLocation(),
                        new Location(
                                Bukkit.getWorld(config.getString("world", event.getPlayer().getWorld().getName())),
                                config.getInt("x"),
                                config.getInt("y"),
                                config.getInt("z")),
                        config.getInt("radius", 0)
                )
        );
    }
}
