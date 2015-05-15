package de.raidcraft.api.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * @author Philip Urban
 */
public interface PersistentInventory {

    /**
     * Returns the unique database ID of the persistent backpack.
     * ID is 0 if backpack isn't stored yet.
     *
     * @return unique database ID
     */
    int getId();

    /**
     * Gets the current title of the inventory.
     *
     * @return inventory title
     */
    String getTitle();

    /**
     * Sets a new title for the inventory, closing and opening the inventory.
     *
     * @param title to set
     */
    void setTitle(String title);

    /**
     * Return the backpack inventory.
     *
     * @return backpack inventory
     */
    Inventory getInventory();

    /**
     * Sets the bukkit inventory reference.
     *
     * @param inventory to set
     */
    void setInventory(Inventory inventory);

    /**
     * Opens the inventory for given player.
     * Register open inventory to track and save changes.
     *
     * @param player Inventory owner
     */
    void open(Player player);

    /**
     * Opens the inventory for given player.
     * Register open inventory to track and save changes.
     * Changed inventory title to given string.
     *
     * @param player   Inventory owner
     * @param newTitle Inventory title
     */
    void open(Player player, String newTitle);

    /**
     * Closes the inventory of the player and saves it to the database.
     *
     * @param player to close inventory for
     */
    void close(Player player);

    /**
     * Closes the inventory of the given player.
     *
     * @param player to close inventory for
     * @param save   true if inventory should be saved
     */
    void close(Player player, boolean save);

    /**
     * Closes all open inventories saving them to the database.
     */
    void closeAll();

    /**
     * Close if inventory is opened by a player.
     * Save changes if flag is set to true.
     *
     * @param save true if changes should be saved
     */
    void closeAll(boolean save);

    /**
     * Creates a new bukkit inventory with the given title and size.
     *
     * @param title of the inventory
     * @param size  of the inventory
     *
     * @return created inventory
     */
    Inventory createNewInventory(String title, int size);

    /**
     * By calling this method the database entries of this inventory will be updated.
     */
    void save();

    /**
     * Removes inventory from database
     */
    void delete();
}
