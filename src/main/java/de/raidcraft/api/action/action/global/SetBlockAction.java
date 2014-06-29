package de.raidcraft.api.action.action.global;

import de.raidcraft.api.action.action.Action;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * @author mdoering
 */
public class SetBlockAction implements Action<Player> {

    @Override
    public void accept(Player player) {

        Location location = new Location(player.getWorld(), getConfig().getInt("x"), getConfig().getInt("y"), getConfig().getInt("z"));
        Material material = Material.getMaterial(getConfig().getString("block", "minecraft:air"));
        if(material == null) {
            return;
        }
        location.getBlock().setType(material);
    }
}
