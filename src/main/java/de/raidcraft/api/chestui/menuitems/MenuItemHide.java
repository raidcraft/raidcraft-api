package de.raidcraft.api.chestui.menuitems;

import de.raidcraft.api.items.RC_Items;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author Dragonfire
 */
public class MenuItemHide extends MenuItem {
    @Getter
    private boolean hidden;
    private ItemStack store;
    private ItemStack empty = new ItemStack(Material.AIR);

    public MenuItemHide(Material type, String name) {
        this(new ItemStack(type), name);
    }

    public MenuItemHide(ItemStack item, String name) {
        store = RC_Items.setDisplayName(item, name);
        setItem(store);
    }


    public void toggle(boolean hideNow) {
        // if not changed
        if(hideNow == hidden) {
            return;
        }
        hidden = hideNow;
        setItem((hidden) ? empty : store);
    }
}
