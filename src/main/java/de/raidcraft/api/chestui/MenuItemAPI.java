package de.raidcraft.api.chestui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Dragonfire
 */
interface MenuItemAPI {

    public ItemStack getItem();

    public void trigger(Player player);
}
