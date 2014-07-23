package de.raidcraft.api.chestui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Dragonfire
 */
interface MenuItemAPI {
    public ItemStack getItem();

    public void trigger(Player player);

    public static MenuItemAPI getPlus(String name) {
        return new MenuItem(Material.INK_SACK);
    }

    public static MenuItemAPI getMinus(String name) {
        return new MenuItem(Material.INK_SACK);
    }

    public static MenuItemAPI getOk(String name) {
        return new MenuItem(Material.GREEN_RECORD);
    }

    public static MenuItemAPI getCancel(String name) {
        return new MenuItem(Material.RECORD_4);
    }
}
