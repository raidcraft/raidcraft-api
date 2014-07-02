package de.raidcraft.api.action.action.global;

import com.sk89q.minecraft.util.commands.CommandContext;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.config.builder.ConfigBuilder;
import de.raidcraft.api.config.builder.ConfigBuilderException;
import de.raidcraft.util.BlockUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.material.Openable;

/**
 * @author mdoering
 */
public class DoorAction implements Action<Player> {

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

    @Information(
            value = "door.toggle",
            desc = "Toggles (-t) the targeted door into an open (-o) or close state.",
            usage = "[-o/-t]",
            flags = "ot",
            help = "Target the openable block (door, trapdoor, gate, etc.) you want to toggle.",
            multiSection = true
    )
    public <T extends BasePlugin> void build(ConfigBuilder<T> builder, CommandContext args, Player player) throws ConfigBuilderException {

        ConfigurationSection config = createConfigSection();
        Block block = BlockUtil.getTargetBlock(player, b -> b.getState().getData() instanceof Openable);
        if (block == null) {
            throw new ConfigBuilderException("No openable block (door, trapdoor, gate, etc.) found in sight!");
        }
        config.set("location", createLocationSection(block.getLocation()));
        config.set("toggle", args.hasFlag('t'));
        config.set("open", args.hasFlag('o'));
        builder.append(this, config, getPath(), ActionAPI.getIdentifier(this));
    }
}
