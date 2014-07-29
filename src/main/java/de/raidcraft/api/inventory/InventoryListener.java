package de.raidcraft.api.inventory;

import de.raidcraft.RaidCraft;
import org.bukkit.entity.HumanEntity;
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
            for (HumanEntity human : inventory.getInventory().getViewers()) {
                if (event.getPlayer().getName().equalsIgnoreCase(human.getName())) {
                    inventory.save();
                    return;
                }
            }
        }
    }
}
