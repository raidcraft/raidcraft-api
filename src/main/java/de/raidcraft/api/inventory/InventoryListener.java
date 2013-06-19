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

        PersistentInventoryManager manager = RaidCraft.getComponent(PersistentInventoryManager.class);
        PersistentInventory persistentInventory = manager.getOpenedInventory(event.getInventory());
        if(persistentInventory == null) return;

        persistentInventory.save();
        manager.closeInventory(persistentInventory);
    }
}
