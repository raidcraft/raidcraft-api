package de.raidcraft.api.chestui.menuitems;

import de.raidcraft.api.items.RC_Items;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Central Class for all MenuItems.
 * Provides some central Items, e.g. back, forward, ok, cancel, pageitem
 * Subclass this and override trigger method.
 *
 * @author Dragonfire
 */
public abstract class MenuItemAPI {

    @Getter
    @Setter
    private int slot;
    @Getter
    @Setter
    private Inventory inventory;

    private ItemStack item;

    public MenuItemAPI setItem(ItemStack item, String name) {

        return setItem(RC_Items.setDisplayName(item, name));

    }

    public MenuItemAPI setItem(Material type, String name, int amount) {

        return setItem(RC_Items.setDisplayName(new ItemStack(type, amount), name));
    }

    public MenuItemAPI setItem(Material type, String name) {

        return setItem(type, name, 1);
    }

    public MenuItemAPI setItem(ItemStack item) {

        this.item = item;
        if (inventory != null) {
            inventory.setItem(slot, this.item);
        }
        return this;
    }

    public ItemStack getItem() {

        if (inventory == null) {
            return item;
        }
        return inventory.getItem(slot);
    }

    public abstract void trigger(Player player);


    public static ItemStack getItemPlus() {

        return getItemPlus("Nächste Seite");
    }

    public static ItemStack getItemPlus(String name) {

        return RC_Items.setDisplayName(RC_Items.createDye(DyeColor.MAGENTA), name);
    }


    public static ItemStack getItemMinus() {

        return getItemPlus("Vorherige Seite");
    }

    public static ItemStack getItemMinus(String name) {

        return RC_Items.setDisplayName(RC_Items.createDye(DyeColor.LIME), name);
    }

    public static ItemStack getItemPage() {

        return getItemPage("Seite");
    }

    public static ItemStack getItemPage(String name) {

        return RC_Items.createItem(Material.BOOK, name);
    }


    public static ItemStack getItemOk() {

        return getItemOk("Ok");
    }

    public static ItemStack getItemOk(String name) {

        return RC_Items.createItem(Material.GREEN_RECORD, name);
    }

    public static ItemStack getItemCancel() {

        return getItemCancel("Abbrechen");
    }

    public static ItemStack getItemCancel(String name) {

        return RC_Items.createItem(Material.RECORD_4, name);
    }
}