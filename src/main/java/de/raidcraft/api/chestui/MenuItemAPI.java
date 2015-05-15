package de.raidcraft.api.chestui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Dragonfire
 */
interface MenuItemAPI {

    ItemStack getItem();

    void trigger(Player player);
}
