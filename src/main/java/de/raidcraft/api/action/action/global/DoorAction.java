package de.raidcraft.api.action.action.global;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.util.ConfigUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.material.Openable;

/**
 * @author mdoering
 */
public class DoorAction implements Action<Player> {

    @Override
    @Information(
            value = "door.toggle",
            desc = "Toggles (-t) the targeted door into an open (-o) or close state.",
            conf = {
                    "location: world,x,y,z coordinates of the door base",
                    "toggle: false/true will toggle to the opposite state",
                    "open: true/false will open/close the door"
            }
    )
    public void accept(Player player, ConfigurationSection config) {

        Location location = ConfigUtil.getLocationFromConfig(config, player);

        Block block = location.getBlock();
        if (block.getState() instanceof Openable) {
            Openable state = (Openable) block.getState();
            boolean open = (config.getBoolean("toggle", false) ? !state.isOpen() : config.getBoolean("open", true));
            state.setOpen(open);
            block.getState().update();
        }
    }
}
