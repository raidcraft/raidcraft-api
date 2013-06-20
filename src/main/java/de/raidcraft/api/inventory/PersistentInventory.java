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
    public int getId();

    /**
     * Gets the current title of the inventory.
     *
     * @return inventory title
     */
    public String getTitle();

    /**
     * Sets a new title for the inventory, closing and opening the inventory.
     *
     * @param title to set
     */
    public void setTitle(String title);

    /**
     * Return the backpack inventory.
     *
     * @return backpack inventory
     */
    public Inventory getInventory();

    /**
     * Sets the bukkit inventory reference.
     *
     * @param inventory to set
     */
    public void setInventory(Inventory inventory);

    /**
     * Opens the inventory for given player.
     * Register open inventory to track and save changes.
     *
     * @param player Inventory owner
     */
    public void open(Player player);

    /**
     * Opens the inventory for given player.
     * Register open inventory to track and save changes.
     * Changed inventory title to given string.
     *
     * @param player Inventory owner
     * @param newTitle Inventory title
     */
    public void open(Player player, String newTitle);

    /**
     * Closes the inventory of the player and saves it to the database.
     *
     * @param player to close inventory for
     */
    public void close(Player player);

    /**
     * Closes the inventory of the given player.
     *
     * @param player to close inventory for
     * @param save true if inventory should be saved
     */
    public void close(Player player, boolean save);

    /**
     * Closes all open inventories saving them to the database.
     */
    public void closeAll();

    /**
     * Close if inventory is opened by a player.
     * Save changes if flag is set to true.
     *
     * @param save true if changes should be saved
     */
    public void closeAll(boolean save);

    /**
     * Creates a new bukkit inventory with the given title and size.
     *
     * @param title of the inventory
     * @param size of the inventory
     * @return created inventory
     */
    public Inventory createNewInventory(String title, int size);

    /**
     * By calling this method the database entries of this inventory will be updated.
     *
     */
    public void save();

    /**
     * Removes inventory from database
     *
     */
    public void delete();
}
