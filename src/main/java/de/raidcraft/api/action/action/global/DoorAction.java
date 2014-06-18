package de.raidcraft.api.action.action.global;

import de.raidcraft.api.action.action.Action;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.Openable;

/**
 * @author mdoering
 */
public class DoorAction implements Action<Player> {

    @Override
    public void accept(Player player) {

        Location location = new Location(player.getWorld(), getConfig().getInt("x"), getConfig().getInt("y"), getConfig().getInt("z"));

        Block block = location.getBlock();
        if(block.getState() instanceof Openable) {
            Openable state = (Openable) block.getState();
            boolean open = (getConfig().getBoolean("toggle", false) ? !state.isOpen() :getConfig().getBoolean("open", true));
            state.setOpen(open);
            block.getState().update();
        }
    }
}
