package de.raidcraft.api.chestui.menuitems;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A Menu Item without any function if a player click it.
 * Overwrite #trigger method(Player) to implement an reaction.
 *
 * @author Dragonfire
 */
public class MenuItemAllowedPlacing extends MenuItemAPI {

    private ItemStack itemStack;

    @Override
    public void trigger(Player player) {
        // nothing
    }

    @Override
    public boolean checkPlacing(ItemStack itemstack) {
        this.itemStack = itemstack;
        return true;
    }

    public ItemStack getItem() {
        return itemStack;
    }
}
