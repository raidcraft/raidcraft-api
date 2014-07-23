package de.raidcraft.api.chestui;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Dragonfire
 */
public class MenuItem implements MenuItemAPI {
    @Getter
    @Setter
    private ItemStack item;

    public MenuItem() {
        item = new ItemStack(Material.AIR);
    }

    public MenuItem(Material type, String name, int amount) {
        item = new ItemStack(type, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
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
