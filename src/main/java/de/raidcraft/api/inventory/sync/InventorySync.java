package de.raidcraft.api.inventory.sync;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.inventory.InvalidInventoryException;
import de.raidcraft.api.inventory.InventoryManager;
import de.raidcraft.api.inventory.PersistentInventory;
import de.raidcraft.api.storage.ItemStorage;
import de.raidcraft.api.storage.ObjectStorage;
import de.raidcraft.api.storage.StorageException;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Save Inventory (Player, Armor) to the database.
 *
 * @author Dragonfire
 */
@Getter
public class InventorySync implements Listener {
    final private InventoryManager inventoryManager;
    final private ObjectStorage<ItemStack> armorStorage;
    private RaidCraftPlugin plugin;
    private Set<UUID> loginLockCache = new HashSet<>();

    public InventorySync(RaidCraftPlugin plugin) {
        this.plugin = plugin;
        this.inventoryManager = RaidCraft.getComponent(InventoryManager.class);
        this.armorStorage = new ItemStorage("PersistentArmor");
    }

    private TPlayerInventory getPlayerInventory(Player player) {
        return plugin.getRcDatabase().find(TPlayerInventory.class)
                .where()
                .eq("player", player.getUniqueId())
                .findOne();
    }

    private void createInventory(Player player) {
        PersistentInventory persistentInventory = inventoryManager.createInventory(player.getInventory());
        persistentInventory.save();

        TPlayerInventory tPlayerInventory = new TPlayerInventory();
        tPlayerInventory.setCreatedAt(new Date());
        tPlayerInventory.setUpdatedAt(tPlayerInventory.getCreatedAt());
        tPlayerInventory.setPlayer(player.getUniqueId());
        tPlayerInventory.setInventoryId(persistentInventory.getId());
        tPlayerInventory.setArmor(player.getInventory(), this.armorStorage);
        tPlayerInventory.setExp(player.getExp());
        tPlayerInventory.setLevel(player.getLevel());
        tPlayerInventory.setCreatedAt(new Date(System.currentTimeMillis()));
        tPlayerInventory.setLocked(false);
        plugin.getRcDatabase().save(tPlayerInventory);
    }

    private void saveInventoryAndRemoveLock(Player player) {

        if(loginLockCache.contains(player.getUniqueId())) {
            return;
        }

        TPlayerInventory tPlayerInventory = getPlayerInventory(player);
        if (tPlayerInventory == null) {
            throw new IllegalArgumentException(player.getUniqueId() + "TPlayerInventory shoud not be null");
        }
        try {
            PersistentInventory persistentInventory = inventoryManager.getInventory(tPlayerInventory.getInventoryId());
            persistentInventory.setInventory(player.getInventory());
            tPlayerInventory.setArmor(player.getInventory(), this.armorStorage);
            tPlayerInventory.setExp(player.getExp());
            tPlayerInventory.setLevel(player.getLevel());
            tPlayerInventory.setUpdatedAt(new Date(System.currentTimeMillis()));
            persistentInventory.save();
        } catch (InvalidInventoryException e) {
            plugin.getLogger().warning("InvalidInventoryException@" + player.getPlayerListName() + ":");
            e.printStackTrace();
        }

        // delete inventory and remove lock
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setExp(0);
        player.setLevel(0);
        tPlayerInventory.setLocked(false);
        plugin.getRcDatabase().update(tPlayerInventory);
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void playerQuitEvent(PlayerQuitEvent event) {
        saveInventoryAndRemoveLock(event.getPlayer());
    }

    // playerQuitEvent is also called on kick
//    @EventHandler(priority = EventPriority.HIGH)
//    public void playerKickEvent(PlayerKickEvent event) {
//        saveInventoryAndRemoveLock(event.getPlayer());
//    }

    @EventHandler(priority = EventPriority.LOW)
    public void playerJoinEvent(final PlayerJoinEvent event) {

        loginLockCache.add(event.getPlayer().getUniqueId());

        TPlayerInventory inventory = plugin.getRcDatabase().find(TPlayerInventory.class)
                .where()
                .eq("player", event.getPlayer().getUniqueId())
                .findOne();

        // if player is the first time on the server or inventory were deleted
        if (inventory == null) {
            createInventory(event.getPlayer());
        }
        // check if inventory is locked and we must delay the load
        DelayedInventoryLoader task = new DelayedInventoryLoader();
        task.setPlayer(event.getPlayer());
        int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, task, 2, 10);
        task.setTaskId(id);
    }

    /**
     * If a player switch between serverss the join event of the target server is async to the save
     * event of source server. To prevent this we wait until the lock in the database is gone.
     *
     * @author Dragonfire
     */
    @Data
    public class DelayedInventoryLoader implements Runnable {
        private Player player;
        private int taskId;
        private boolean finished = false;
        private int i = 0;

        public void run() {
            // remove this task after a while to avoid performance issues
            i++;
            if (i > 100) {
                finished = true;
            }
            if (finished) {
                if (taskId <= 0) {
                    // task id is maybe not set, depend on Bukkit Scheduler
                    throw new IllegalArgumentException(player.getUniqueId() + "taskId should be > 0");
                }
                Bukkit.getScheduler().cancelTask(this.taskId);
                return;
            }
            TPlayerInventory inventory = getPlayerInventory(player);
            // only restore Inventory if saving is complete
            if (!inventory.isLocked()) {
                try {
                    PersistentInventory persistentInventory = inventoryManager.getInventory(inventory.getInventoryId());
                    player.getInventory().setContents(persistentInventory.getInventory().getContents());
                    loadArmor(inventory);
                    if(inventory.getExp() != -1 && inventory.getLevel() != -1) {
                        player.setExp(inventory.getExp());
                        player.setLevel(inventory.getLevel());
                    }
                } catch (InvalidInventoryException e) {
                    plugin.getLogger().warning("InvalidInventoryException@" + player.getPlayer().getPlayerListName()
                            + " Inventory:" + inventory.getInventoryId());
                    e.printStackTrace();
                }
                inventory.setLocked(true);
                finished = true;
                plugin.getRcDatabase().update(inventory);
                loginLockCache.remove(player.getUniqueId());
            }

        }

        private void loadArmor(TPlayerInventory tPlayerInventory) {
            PlayerInventory playerInventory = this.player.getInventory();
            try {
                playerInventory.setHelmet(armorStorage.getObject(tPlayerInventory.getObjectHelmet()));
            } catch (StorageException e) {
                e.printStackTrace();
            }
            try {
                playerInventory.setChestplate(armorStorage.getObject(tPlayerInventory.getObjectChestplate()));
            } catch (StorageException e) {
                e.printStackTrace();
            }
            try {
                playerInventory.setLeggings(armorStorage.getObject(tPlayerInventory.getObjectLeggings()));
            } catch (StorageException e) {
                e.printStackTrace();
            }
            try {
                playerInventory.setBoots(armorStorage.getObject(tPlayerInventory.getObjectBoots()));
            } catch (StorageException e) {
                e.printStackTrace();
            }
        }
    }
}
