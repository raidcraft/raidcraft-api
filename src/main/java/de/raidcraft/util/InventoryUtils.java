package de.raidcraft.util;


import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * @author Dragonfire
 */
public class InventoryUtils {
    public final static int COLUMN_COUNT = 9;
    public final static int MAX_ROWS = 6;

    /**
     * Tries to add the given {@link ItemStack} to the players inventory.
     * If the inventory is full every available space is filled and the rest is dropped in front of the {@link Player}.
     *
     * @param player to add items to
     * @param items  to add
     */
    public static void addOrDropItems(Player player, ItemStack... items) {

        HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(items);
        leftovers.values().forEach(item -> player.getWorld().dropItem(player.getLocation(), item));
    }

    /**
     * Sets the given {@link ItemStack} in the @{@link Player} {@link org.bukkit.inventory.Inventory}
     * at the given {@param slot}. If there is an existing item at the slot it will add it the Inventory
     * using {@link #addOrDropItems(Player, ItemStack...)}.
     *
     * @param player to set the item stack to
     * @param item   to set
     * @param slot   to set at
     */
    public static void setAndDropOrAddItem(Player player, ItemStack item, int slot) {

        ItemStack previousItem = player.getInventory().getItem(slot);
        player.getInventory().setItem(slot, item);
        if (previousItem != null) addOrDropItems(player, previousItem);
    }
}
