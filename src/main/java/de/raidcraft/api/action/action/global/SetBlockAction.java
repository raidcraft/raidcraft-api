package de.raidcraft.api.action.action.global;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.util.ConfigUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author mdoering
 */
public class SetBlockAction implements Action<Player> {

    @Override
    @Information(
            value = "block.set",
            desc = "Sets a block at the given x,y,z coordinates.",
            conf = {
                    "world: [current]",
                    "x",
                    "y",
                    "z",
                    "block: DIRT"
            }
    )
    public void accept(Player player, ConfigurationSection config) {

        Location location = ConfigUtil.getLocationFromConfig(config, player);
        Material material = Material.matchMaterial(config.getString("block", "minecraft:air"));
        if (material == null) {
            RaidCraft.LOGGER.warning("Unknown block material " + config.getString("block") + " in block.set withAction!");
            return;
        }
        if (location == null) return;
        location.getBlock().setType(material);
    }
}
