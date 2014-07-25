package de.raidcraft.api.chestui.menuitems;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * A MenuItem with two states: visible - hidden.
 * Each state has his own item.
 *
 * @author Dragonfire
 */
public class MenuItemHide extends MenuItem {

    @Getter
    private boolean hidden;
    private ItemStack store;
    private ItemStack empty = new ItemStack(Material.AIR);

    public MenuItemHide setVisibleItem(ItemStack item) {

        this.store = item;
        setItem(store);
        return this;
    }

    public MenuItemHide setHiddenItem(ItemStack item) {

        this.empty = item;
        return this;
    }


    public void toggle(boolean hideNow) {
        // if not changed
        if (hideNow == hidden) {
            return;
        }
        hidden = hideNow;
        setItem((hidden) ? empty : store);
    }
}
