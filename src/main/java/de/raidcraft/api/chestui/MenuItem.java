package de.raidcraft.api.chestui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
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

    public MenuItem(Material type, int amount) {
        item = new ItemStack(type, amount);
    }

    public MenuItem(Material type) {
        this(type, 0);
    }

    @Override
    public  ItemStack getItem() {
        return item;
    }

    @Override
    public void trigger(Player player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
