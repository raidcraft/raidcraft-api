package de.raidcraft.api.chestui.menuitems;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A Menu Item without any function if a player click it.
 * Overwrite #trigger method(Player) to implement an reaction.
 *
 * @author Dragonfire
 */
public class MenuItem extends MenuItemAPI {

    @Override
    public void trigger(Player player) {
        // nothing
    }

    @Override
    public boolean checkPlacing(ItemStack itemstack) {
        return false;
    }
}
