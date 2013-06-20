package de.raidcraft.api.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * @author Philip Urban
 */
public interface PersistentInventory {

    /*
     * Return the backpack inventory.
     *
     * @Return backpack inventory
     */
    public Inventory getInventory();

    /*
     * Returns the unique database ID of the persistent backpack.
     * ID is 0 if backpack isn't stored yet.
     *
     * @Return unique database ID
     */
    public int getId();

    /*
     * By calling this method the database entries of this inventory will be updated.
     *
     */
    public void save();

    /*
     * Removes inventory from database
     *
     */
    public void delete();

    /*
     * Opens the inventory for given player.
     * Register open inventory to track and save changes.
     *
     * @Param Inventory owner
     */
    public void open(Player player);

    /*
     * Opens the inventory for given player.
     * Register open inventory to track and save changes.
     * Changed inventory title to given string.
     *
     * @Param Inventory owner
     */
    public void open(Player player, String newTitle);

    /*
     * Close if inventory is opened by a player.
     * Save changes if flag is set to true.
     *
     * @Param Flag if changes should be saved
     */
    public void close(boolean save);
}
