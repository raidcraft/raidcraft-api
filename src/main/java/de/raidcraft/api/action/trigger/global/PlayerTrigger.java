package de.raidcraft.api.action.trigger.global;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.trigger.Trigger;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.util.ConfigUtil;
import de.raidcraft.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Silthus
 */
public class PlayerTrigger extends Trigger implements Listener {

    public PlayerTrigger() {

        super("player", "interact", "block.break", "block.place", "move", "craft", "death", "join");
    }

    @Information(
            value = "player.interact",
            desc = "Listens for player interaction (with certain blocks at the defined location).",
            conf = {
                    "x",
                    "y",
                    "z",
                    "world",
                    "type: DIRT"
            }
    )
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {

        informListeners("interact", event.getPlayer(), config -> {

            Block block = event.getClickedBlock();
            if (config.isSet("x") && config.isSet("y") && config.isSet("z")) {
                Location blockLocation = block.getLocation();
                if (blockLocation.getBlockX() != config.getInt("x")
                        || blockLocation.getBlockY() != config.getInt("y")
                        || blockLocation.getBlockZ() != config.getInt("z")
                        || !blockLocation.getWorld().equals(Bukkit.getWorld(config.getString("world", blockLocation.getWorld().getName())))) {
                    return false;
                }
            }
            Set<Material> blocks = new HashSet<>();
            if (config.isList("blocks")) {
                config.getStringList("blocks").forEach(b -> {
                    Material material = Material.matchMaterial(b);
                    if (material != null) {
                        blocks.add(material);
                    } else {
                        RaidCraft.LOGGER.warning("Wrong block defined in player.interact trigger! " + ConfigUtil.getFileName(config));
                    }
                });
                return blocks.contains(block.getType());
            }
            return !config.isSet("block") || Material.matchMaterial(config.getString("block", "AIR")) == block.getType();
        });
    }

    @Information(
            value = "player.craft",
            desc = "Listens for crafting of items (can be custom).",
            conf = {
                    "item: <rc1337/so43034/world.quest.named-item/WOOD:5>"
            }
    )
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCraft(CraftItemEvent event) {

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        informListeners("craft", event.getWhoClicked(), config -> {
            try {
                return !config.isSet("item") || RaidCraft.getItem(config.getString("item")).isSimilar(event.getRecipe().getResult());
            } catch (CustomItemException e) {
                return false;
            }
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {

        informListeners("death", event.getEntity());
    }

    @Information(
            value = "player.block.break",
            desc = "Listens for block breaking and optionally at the defined location.",
            conf = {
                    "blocks: List of blocks to listen for (overrides a single block)",
                    "block: DIRT",
                    "world: [current]",
                    "x",
                    "y",
                    "z"
            }
    )
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {

        informListeners("block.break", event.getPlayer(), config -> {

            if (config.isSet("x")) {
                Location location = ConfigUtil.getLocationFromConfig(config, event.getPlayer());
                Location playerLocation = event.getPlayer().getLocation();
                if (!location.getWorld().equals(playerLocation.getWorld())
                        || location.getBlockX() != playerLocation.getBlockX()
                        || location.getBlockY() != playerLocation.getBlockY()
                        || location.getBlockZ() != playerLocation.getBlockZ()) {
                    return false;
                }
            }

            Set<Material> blocks = new HashSet<>();
            if (config.isList("blocks")) {
                config.getStringList("blocks").forEach(b -> {
                    Material material = Material.matchMaterial(b);
                    if (material != null) {
                        blocks.add(material);
                    } else {
                        RaidCraft.LOGGER.warning("Wrong block defined in player.interact trigger! " + ConfigUtil.getFileName(config));
                    }
                });
                return blocks.contains(event.getBlock().getType());
            }

            Material block = Material.matchMaterial(config.getString("block", "AIR"));
            if (block == null) {
                RaidCraft.LOGGER.warning("Wrong block defined in player.interact trigger! " + ConfigUtil.getFileName(config));
            }
            return block == null || block == Material.AIR || block == event.getBlock().getType();
        });
    }

    @Information(
            value = "player.block.place",
            desc = "Listens for block placing and optionally at the defined location.",
            conf = {
                    "blocks: List of blocks to listen for (overrides a single block)",
                    "block: DIRT",
                    "world: [current]",
                    "x",
                    "y",
                    "z"
            }
    )
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {

        informListeners("block.place", event.getPlayer(), config -> {

            if (config.isSet("x")) {
                Location location = ConfigUtil.getLocationFromConfig(config, event.getPlayer());
                Location playerLocation = event.getPlayer().getLocation();
                if (!location.getWorld().equals(playerLocation.getWorld())
                        || location.getBlockX() != playerLocation.getBlockX()
                        || location.getBlockY() != playerLocation.getBlockY()
                        || location.getBlockZ() != playerLocation.getBlockZ()) {
                    return false;
                }
            }

            Set<Material> blocks = new HashSet<>();
            if (config.isList("blocks")) {
                config.getStringList("blocks").forEach(b -> {
                    Material material = Material.matchMaterial(b);
                    if (material != null) {
                        blocks.add(material);
                    } else {
                        RaidCraft.LOGGER.warning("Wrong block defined in player.interact trigger! " + ConfigUtil.getFileName(config));
                    }
                });
                return blocks.contains(event.getBlock().getType());
            }

            Material block = Material.matchMaterial(config.getString("block", "AIR"));
            if (block == null) {
                RaidCraft.LOGGER.warning("Wrong block defined in player.interact trigger! " + ConfigUtil.getFileName(config));
            }
            return block == null || block == Material.AIR || block == event.getBlock().getType();
        });
    }

    @Information(
            value = "player.join",
            desc = "Triggers when a player joined the server."
    )
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {

        informListeners("join", event.getPlayer());
    }

    @Information(
            value = "player.move",
            desc = "Triggers if the player is at or in a radius of the given location.",
            conf = {
                    "world: [current]",
                    "x",
                    "y",
                    "z",
                    "radius: [0]"
            }
    )
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent event) {

        if (!hasMoved(event)) return;

        // if no coordinates are defined always return true
        // otherwise check the coordinates and the radius
        informListeners("move", event.getPlayer(), config -> {

            World world;
            if (config.isSet("world")) {
                world = Bukkit.getWorld(config.getString("world"));
            } else {
                world = event.getPlayer().getWorld();
            }
            if (world == null || !world.equals(event.getPlayer().getWorld())) {
                return false;
            }
            return ((!config.isSet("x") || !config.isSet("y") || !config.isSet("z"))
                    || LocationUtil.isWithinRadius(
                    event.getPlayer().getLocation(),
                    ConfigUtil.getLocationFromConfig(config, event.getPlayer()),
                    config.getInt("radius", 0)
            ));
        });
    }

    private boolean hasMoved(PlayerMoveEvent event) {
        // Did we move a block?
        if (event.getFrom().getBlockX() != event.getTo().getBlockX()
                || event.getFrom().getBlockY() != event.getTo().getBlockY()
                || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            return true;
        }
        return false;
    }
}