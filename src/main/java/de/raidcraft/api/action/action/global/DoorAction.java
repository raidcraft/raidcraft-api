package de.raidcraft.api.action.action.global;

import com.sk89q.minecraft.util.commands.CommandContext;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.ActionAPISectionBuilder;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.config.builder.ConfigBuilderException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.material.Openable;
import org.bukkit.util.BlockIterator;

/**
 * @author mdoering
 */
public class DoorAction implements Action<Player>, ActionAPISectionBuilder {

    @Override
    public void accept(Player player) {

        ConfigurationSection locationSection = getConfig().getConfigurationSection("location");
        World world = Bukkit.getWorld(locationSection.getString("world"));
        if (world == null && !locationSection.isSet("world")) {
            world = player.getWorld();
        } else {
            RaidCraft.LOGGER.warning("Unknown world defined in door action config: " + getConfig().getName());
            return;
        }
        Location location = new Location(world, locationSection.getInt("x"), locationSection.getInt("y"), locationSection.getInt("z"));

        Block block = location.getBlock();
        if (block.getState() instanceof Openable) {
            Openable state = (Openable) block.getState();
            boolean open = (getConfig().getBoolean("toggle", false) ? !state.isOpen() : getConfig().getBoolean("open", true));
            state.setOpen(open);
            block.getState().update();
        }
    }

    @Override
    @Information(
            value = "action.toggledoor",
            desc = "Toggles (-t) the targeted door into an open (-o) or close state.",
            usage = "[-o/-t]",
            flags = "ot",
            help = "Target the openable block (door, trapdoor, gate, etc.) you want to toggle."
    )
    public ConfigurationSection createArgsSection(CommandContext args, Player player) throws ConfigBuilderException {

        ConfigurationSection config = new MemoryConfiguration();
        BlockIterator blockIterator = new BlockIterator(player, 25);
        boolean foundBlock = false;
        while (blockIterator.hasNext()) {
            Block block = blockIterator.next();
            if (block.getState().getData() instanceof Openable) {
                // we found our door type thingy
                ConfigurationSection location = config.createSection("location");
                location.set("world", player.getWorld().getName());
                location.set("x", block.getLocation().getBlockX());
                location.set("y", block.getLocation().getBlockY());
                location.set("z", block.getLocation().getBlockZ());
                foundBlock = true;
                break;
            }
        }
        if (!foundBlock) {
            throw new ConfigBuilderException("No openable block (door, trapdoor, gate, etc.) found in sight!");
        }
        config.set("toggle", args.hasFlag('t'));
        config.set("open", args.hasFlag('o'));
        return config;
    }
}
