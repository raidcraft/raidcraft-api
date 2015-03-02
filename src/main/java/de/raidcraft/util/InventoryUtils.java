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

    public static void addOrDropItems(Player player, ItemStack... items) {

        HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(items);
        leftovers.values().forEach(item -> player.getWorld().dropItem(player.getLocation(), item));
    }
}
