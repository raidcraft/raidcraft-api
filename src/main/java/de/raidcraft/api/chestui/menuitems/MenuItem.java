package de.raidcraft.api.chestui.menuitems;

import de.raidcraft.api.items.RC_Items;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Dragonfire
 */
public class MenuItem extends MenuItemAPI {

    public MenuItem(ItemStack item) {

        setItem(item);
    }

    public MenuItem(ItemStack item, String name) {
        this(RC_Items.setDisplayName(item, name));
    }

    public MenuItem() {

        this(new ItemStack(Material.AIR));
    }

    public MenuItem(Material type, String name, int amount) {

        this(RC_Items.setDisplayName(new ItemStack(type, amount), name));
    }

    public MenuItem(Material type, String name) {

        this(type, name, 0);
    }

    @Override
    public void trigger(Player player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public static MenuItemAPI getPlus(String name) {

        return new MenuItem(Material.INK_SACK, name);
    }

    public static MenuItemAPI getMinus(String name) {

        return new MenuItem(Material.INK_SACK, name);
    }

    public static MenuItemAPI getOk(String name) {

        return new MenuItem(Material.GREEN_RECORD, name);
    }

    public static MenuItemAPI getCancel(String name) {

        return new MenuItem(Material.RECORD_4, name);
    }
}
