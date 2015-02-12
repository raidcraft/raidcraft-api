package de.raidcraft.api.action.action.global;

import com.sk89q.minecraft.util.commands.CommandContext;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.config.builder.ConfigBuilder;
import de.raidcraft.api.config.builder.ConfigBuilderException;
import de.raidcraft.util.BlockUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author mdoering
 */
public class SetBlockAction implements Action<Player> {

    @Override
    public void accept(Player player, ConfigurationSection config) {

        Location location = new Location(player.getWorld(), config.getInt("x"), config.getInt("y"), config.getInt("z"));
        Material material = Material.getMaterial(config.getString("block", "minecraft:air"));
        if (material == null) {
            return;
        }
        location.getBlock().setType(material);
    }

    @Information(
            value = "block.set",
            desc = "Sets a block at the given x,y,z coordinates.",
            help = "Target the block type and location you want to set with your cursor.",
            conf = {
                "x",
                "y",
                "z",
                "block: e.g.: minecraft:air"
            },
            multiSection = true
    )
    public <T extends BasePlugin> void build(ConfigBuilder<T> builder, CommandContext args, Player player) throws ConfigBuilderException {

        ConfigurationSection config = createConfigSection();
        Block block = BlockUtil.getTargetBlock(player);
        if (block == null) throw new ConfigBuilderException("No valid target block found in crosshair.");
        config.set("block", block.getType());
        config.set("data", block.getState().getData());
        config.set("location", createLocationSection(block.getLocation()));
        builder.append(this, config, getPath(), "block.set");
    }
}
