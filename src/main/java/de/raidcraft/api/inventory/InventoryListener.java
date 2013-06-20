package de.raidcraft.api.inventory;

import de.raidcraft.RaidCraft;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * @author Philip Urban
 */
public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        InventoryManager manager = RaidCraft.getComponent(InventoryManager.class);
        for (PersistentInventory inventory : manager.getLoadedInventories()) {
            if (event.getInventory().equals(inventory.getInventory())) {
                inventory.save();
                return;
            }
        }
    }
}
