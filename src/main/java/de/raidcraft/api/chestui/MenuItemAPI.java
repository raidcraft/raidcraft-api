package de.raidcraft.api.chestui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Dragonfire
 */
public interface MenuItemAPI {
    public ItemStack getItem();

    public void setItem(ItemStack item);

    public void trigger(Player player);
}
