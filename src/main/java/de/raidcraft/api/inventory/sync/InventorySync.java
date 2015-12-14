package de.raidcraft.api.inventory.sync;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.inventory.InvalidInventoryException;
import de.raidcraft.api.inventory.InventoryManager;
import de.raidcraft.api.inventory.PersistentInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Dragonfire
 */
public class InventorySync implements Listener {
    private InventoryManager inventoryManager;
    private RaidCraftPlugin plugin;

    public InventorySync(RaidCraftPlugin plugin) {
        this.plugin = plugin;
        this.inventoryManager = RaidCraft.getComponent(InventoryManager.class);
    }

    private TPlayerInventory getPlayerInventory(Player player) {
        return plugin.getDatabase().find(TPlayerInventory.class)
                .where()
                .eq("player", player.getUniqueId())
                .findUnique();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void playerQuitEvent(PlayerQuitEvent event) {
        TPlayerInventory playerInventory = getPlayerInventory(event.getPlayer());
        if (playerInventory == null) {
            PersistentInventory persistentInventory = inventoryManager.createInventory(event.getPlayer().getInventory());
            persistentInventory.save();
            inventoryManager.unloadInventory(persistentInventory);

            TPlayerInventory inventory = new TPlayerInventory();
            inventory.setPlayer(event.getPlayer().getUniqueId());
            inventory.setInventoryId(persistentInventory.getId());
            plugin.getDatabase().save(inventory);
        } else {
            try {
                PersistentInventory persistentInventory = inventoryManager.getInventory(playerInventory.getInventoryId());
                persistentInventory.setInventory(event.getPlayer().getInventory());
                persistentInventory.save();
                inventoryManager.unloadInventory(persistentInventory);
            } catch (InvalidInventoryException e) {
                plugin.getLogger().warning("InvalidInventoryException@" + event.getPlayer().getPlayerListName() + ":");
                e.printStackTrace();
            }
        }
        // delete inventory
        event.getPlayer().getInventory().clear();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void playerJoinEvent(PlayerJoinEvent event) {
        TPlayerInventory inventory = plugin.getDatabase().find(TPlayerInventory.class)
                .where()
                .eq("player", event.getPlayer().getUniqueId())
                .findUnique();
        if (inventory == null) {
            return;
        }
        try {
            PersistentInventory persistentInventory = this.inventoryManager.getInventory(inventory.getInventoryId());
            event.getPlayer().getInventory().setContents(persistentInventory.getInventory().getContents());
        } catch (InvalidInventoryException e) {
            plugin.getLogger().warning("InvalidInventoryException@" + event.getPlayer().getPlayerListName()
                    + " Inventory:" + inventory.getInventoryId());
            e.printStackTrace();
        }
    }
}
