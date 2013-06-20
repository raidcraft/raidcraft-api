package de.raidcraft.api.inventory;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.storage.ItemStorage;
import de.raidcraft.api.storage.ObjectStorage;
import de.raidcraft.api.storage.StorageException;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Philip Urban
 */
public abstract class AbstractPersistentInventory implements PersistentInventory {

    private PersistentInventoryManager manager;
    private Inventory inventory;

    private TPersistentInventory tPersistentInventory;

    protected AbstractPersistentInventory(int id) {

        manager = RaidCraft.getComponent(PersistentInventoryManager.class);

        if(manager.inventoryIsLoaded(id)) {
            AbstractPersistentInventory loadedInventory = manager.getLoadedInventory(id);
            inventory = loadedInventory.getInventory();
            tPersistentInventory = loadedInventory.gettPersistentInventory();
        }
        else {
            tPersistentInventory = RaidCraft.getDatabase(RaidCraftPlugin.class).find(TPersistentInventory.class, id);
            manager.registerInventory(this);
        }
    }

    protected AbstractPersistentInventory(String title, int size) {

        manager = RaidCraft.getComponent(PersistentInventoryManager.class);
        if(size % 9 != 0) {
            size = (size/9) * 9;
        }
        if(size > 54) size = 54;

        tPersistentInventory = new TPersistentInventory();
        tPersistentInventory.setTitle(title);
        tPersistentInventory.setSize(size);
        tPersistentInventory.setCreated(new Timestamp(System.currentTimeMillis()));
        RaidCraft.getDatabase(RaidCraftPlugin.class).save(tPersistentInventory);
        manager.registerInventory(this);
    }

    @Override
    public Inventory getInventory() {

        return inventory;
    }

    @Override
    public int getId() {

        return tPersistentInventory.getId();
    }

    @Override
    public void save() {

        if(!validate()) return;

        ObjectStorage objectStorage = new ItemStorage("PersistentInventory");
        // delete all stored objects
        for(TPersistentInventorySlot slot : tPersistentInventory.getSlots()) {
            try {
                objectStorage.removeObject(slot.getObjectId());
            } catch (StorageException e) {
            }
        }
        RaidCraft.getDatabase(RaidCraftPlugin.class).delete(tPersistentInventory.getSlots());

        // save new inventory slots
        Set<TPersistentInventorySlot> slots = new HashSet<>(inventory.getSize());
        for(int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if(itemStack == null || itemStack.getType() == Material.AIR) continue;

            int objId = objectStorage.storeObject(itemStack);
            TPersistentInventorySlot slot = new TPersistentInventorySlot();
            slot.setInventoryId(tPersistentInventory.getId());
            slot.setObjectId(objId);
            slot.setSlot(i);
            slots.add(slot);
        }
        tPersistentInventory.setSlots(slots);
        RaidCraft.getDatabase(RaidCraftPlugin.class).save(tPersistentInventory);
    }

    @Override
    public void delete() {

        if(!validate()) return;

        manager.closeInventory(this);
        RaidCraft.getDatabase(RaidCraftPlugin.class).delete(tPersistentInventory);
        tPersistentInventory = null;
        manager.unregisterInventory(this);
    }

    @Override
    public void open(Player player) {

        manager.openInventory(player, this);
        player.openInventory(inventory);
    }

    @Override
    public void open(Player player, String newTitle) {

        if(!newTitle.equals(inventory.getTitle())) {
            tPersistentInventory.setTitle(newTitle);
            RaidCraft.getDatabase(RaidCraftPlugin.class).save(tPersistentInventory);
        }
        manager.openInventory(player, this);
        player.openInventory(inventory);
    }

    @Override
    public void close(boolean save) {

        manager.closeInventory(this);
        if(save) {
            save();
        }
    }

    public boolean validate() {

        return (tPersistentInventory != null);
    }

    public TPersistentInventory gettPersistentInventory() {

        return tPersistentInventory;
    }
}
