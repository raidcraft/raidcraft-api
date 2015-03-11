package de.raidcraft.api.action.trigger.global;

import com.sk89q.minecraft.util.commands.CommandContext;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.trigger.Trigger;
import de.raidcraft.api.config.builder.ConfigBuilder;
import de.raidcraft.api.config.builder.ConfigBuilderException;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.util.BlockUtil;
import de.raidcraft.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * @author Silthus
 */
public class PlayerTrigger extends Trigger implements Listener {

    public PlayerTrigger() {

        super("player", "interact", "block.break", "block.place", "move", "craft", "death");
    }

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
            return !config.isSet("block") || Material.matchMaterial(config.getString("block", "minecraft:air")) == block.getType();
        });
    }

    @Information(
            value = "player.interact",
            desc = "Listens for player interaction (with certain blocks at the defined location).",
            help = "Target the block you want to listen for or define the -x flag to listen for all interacts",
            conf = {
                    "x",
                    "y",
                    "z",
                    "world",
                    "type: e.g. minecraft:dirt"
            },
            usage = "[-x]",
            flags = "x",
            multiSection = true
    )
    public void interact(ConfigBuilder builder, CommandContext args, Player player) throws ConfigBuilderException {

        ConfigurationSection config = createConfigSection(getInformation("player.interact"));
        if (!args.hasFlag('x')) {
            Block block = BlockUtil.getTargetBlock(player);
            if (block == null) throw new ConfigBuilderException("No valid target block found in crosshair!");
            config.set("type", block.getType());
            config.set("location", createLocationSection(block.getLocation()));
        }
        builder.append(this, config, getPath(), "player.interact");
    }

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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {

        informListeners("block.break", event.getPlayer(), config -> {
            Material block = Material.matchMaterial(config.getString("block", "minecraft:air"));
            return block == Material.AIR || block == event.getBlock().getType();
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {

        informListeners("block.place", event.getPlayer(), config -> {
            Material block = Material.matchMaterial(config.getString("block", "minecraft:air"));
            return block == Material.AIR || block == event.getBlock().getType();
        });
    }

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
                    new Location(
                            world,
                            config.getInt("x"),
                            config.getInt("y"),
                            config.getInt("z")),
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