package de.raidcraft.api.inventory.sync;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.inventory.InvalidInventoryException;
import de.raidcraft.api.inventory.InventoryManager;
import de.raidcraft.api.inventory.PersistentInventory;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Dragonfire
 */
public class InventorySync implements Listener {
    final private InventoryManager inventoryManager;
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

    private void saveInventoryAndRemoveLock(Player player) {

        // check if inventory is empty
        boolean found = false;
        for(ItemStack item : player.getInventory().getContents())
        {
            if(item != null && item.getType() != Material.AIR) {
                found = true;
                break;
            }
            found = false;
        }
        if(!found) {
            return;
        }

        TPlayerInventory playerInventory = getPlayerInventory(player);
        if (playerInventory == null) {
            PersistentInventory persistentInventory = inventoryManager.createInventory(player.getInventory());
            persistentInventory.save();

            playerInventory = new TPlayerInventory();
            playerInventory.setPlayer(player.getUniqueId());
            playerInventory.setInventoryId(persistentInventory.getId());
            plugin.getDatabase().save(playerInventory);
        } else {
            try {
                PersistentInventory persistentInventory = inventoryManager.getInventory(playerInventory.getInventoryId());
                persistentInventory.setInventory(player.getInventory());
                persistentInventory.save();
            } catch (InvalidInventoryException e) {
                plugin.getLogger().warning("InvalidInventoryException@" + player.getPlayerListName() + ":");
                e.printStackTrace();
            }
        }
        // delete inventory and remove lock
        player.getInventory().clear();
        player.updateInventory();
        playerInventory.setLocked(false);
        plugin.getDatabase().update(playerInventory);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void playerQuitEvent(PlayerQuitEvent event) {
        saveInventoryAndRemoveLock(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void playerKickEvent(PlayerKickEvent event) {
        saveInventoryAndRemoveLock(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void playerJoinEvent(final PlayerJoinEvent event) {
        TPlayerInventory inventory = plugin.getDatabase().find(TPlayerInventory.class)
                .where()
                .eq("player", event.getPlayer().getUniqueId())
                .findUnique();

        if (inventory != null) {
            // check if inventory is locked and we must delay the load
            BRunnable task = new BRunnable();
            task.setPlugin(plugin);
            task.setPlayer(event.getPlayer());
            int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, task, 0, 10);
            task.setTaskId(id);
        }
    }

    @Data
    public class BRunnable implements Runnable {
        public BasePlugin plugin;
        public Player player;
        public int taskId;
        private boolean finished = false;
        private int i = 0;

        public void run() {
            i++;
            if(i > 100) {
                Bukkit.getScheduler().cancelTask(this.taskId);
            }
            if(finished) {
                if(taskId > 0) {
                    Bukkit.getScheduler().cancelTask(this.taskId);
                }
                return;
            }
            TPlayerInventory inventory = plugin.getDatabase().find(TPlayerInventory.class)
                    .where()
                    .eq("player", player.getUniqueId())
                    .findUnique();
            if(!inventory.isLocked()) {
                try {
                    PersistentInventory persistentInventory = inventoryManager.getInventory(inventory.getInventoryId());
                    player.getInventory().setContents(persistentInventory.getInventory().getContents());
                } catch (InvalidInventoryException e) {
                    plugin.getLogger().warning("InvalidInventoryException@" + player.getPlayer().getPlayerListName()
                            + " Inventory:" + inventory.getInventoryId());
                    e.printStackTrace();
                }
                inventory.setLocked(true);
                plugin.getDatabase().update(inventory);
                finished = true;
            }
        }
    }
}
