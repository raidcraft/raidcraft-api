package de.raidcraft.api.chestui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author Dragonfire
 */
public class MenuItem implements MenuItemAPI {
    private ItemStack item;

    public MenuItem() {
        item = new ItemStack(Material.STAINED_GLASS);
        item.setAmount(99);
    }


    @Override
    public  ItemStack getItem() {
        return item;
    }
}
