package de.raidcraft.api.random;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a dropable object that will spawn as an {@link org.bukkit.inventory.ItemStack}
 * and can be picked up by a player.
 */
public interface Dropable {

    /**
     * Gets the {@link org.bukkit.inventory.ItemStack} that will represent the dropped object.
     */
    public ItemStack getItemStack();

    /**
     * Picks up the dropped item and adds it to the player.
     *
     * @param player to add drop to
     */
    public void pickup(Player player);
}
