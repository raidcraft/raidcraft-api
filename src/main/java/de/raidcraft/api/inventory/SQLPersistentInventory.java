package de.raidcraft.api.inventory;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.storage.ItemStorage;
import de.raidcraft.api.storage.ObjectStorage;
import de.raidcraft.api.storage.StorageException;
import io.ebean.EbeanServer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Philip Urban
 */
public final class SQLPersistentInventory implements PersistentInventory {

    private final int id;
    private Inventory inventory;

    protected SQLPersistentInventory(int id) throws InvalidInventoryException {

        this.id = id;
        setInventory(loadInventory());
    }

    protected SQLPersistentInventory(String title, int size) {

        createNewInventory(title, size);
        this.id = createTableEntry(inventory);
        RaidCraft.getComponent(InventoryManager.class).loadInventory(this);
    }

    protected SQLPersistentInventory(Inventory inventory) {

        this.id = createTableEntry(inventory);
        setInventory(inventory);
    }

    private int createTableEntry(Inventory inventory) {

        // lets create a new db entry
        TPersistentInventory table = new TPersistentInventory();
        table.setCreated(new Timestamp(System.currentTimeMillis()));
        table.setLastUpdate(new Timestamp(System.currentTimeMillis()));
        table.setSize(inventory.getSize());
        table.setTitle(inventory.getTitle());
        RaidCraft.getDatabase(RaidCraftPlugin.class).save(table);
        return table.getId();
    }

    private Inventory loadInventory() throws InvalidInventoryException {

        Inventory inventory;
        EbeanServer database = RaidCraft.getDatabase(RaidCraftPlugin.class);
        TPersistentInventory table = database.find(TPersistentInventory.class, getId());
        if (table == null) {
            inventory = Bukkit.createInventory(null, InventoryType.CHEST);
            table = new TPersistentInventory();
            table.setSize(inventory.getSize());
            table.setTitle(inventory.getTitle());
            table.setLastUpdate(new Timestamp(System.currentTimeMillis()));
            table.setCreated(new Timestamp(System.currentTimeMillis()));
            database.save(table);
        } else {
            inventory = Bukkit.createInventory(null, table.getSize(), table.getTitle());
            ItemStorage storage = new ItemStorage("PersistantInventory");
            // fill the inventory
            for (TPersistentInventorySlot slot : table.getSlots()) {
                try {
                    inventory.setItem(slot.getSlot(), storage.getObject(slot.getObjectId()));
                } catch (StorageException ignored) {
                }
            }
        }
        return inventory;
    }

    @Override
    public Inventory createNewInventory(String title, int size) {

        Inventory newInventory = Bukkit.createInventory(null, size, title);
        if (getInventory() != null) {
            newInventory.addItem(getInventory().getContents());
        }
        setInventory(newInventory);
        return getInventory();
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public String getTitle() {

        return getInventory().getTitle();
    }

    @Override
    public void setTitle(String title) {

        closeAll(true);
        setInventory(createNewInventory(getInventory().getTitle(), getInventory().getSize()));
    }

    @Override
    public Inventory getInventory() {

        return inventory;
    }

    @Override
    public void setInventory(Inventory inventory) {

        this.inventory = inventory;
        if (getId() != 0) {
            RaidCraft.getComponent(InventoryManager.class).loadInventory(this);
        }
    }

    @Override
    public void open(Player player) {

        player.openInventory(getInventory());
    }

    @Override
    public void open(Player player, String newTitle) {

        setTitle(newTitle);
        open(player);
    }

    @Override
    public void close(Player player) {

        close(player, true);
    }

    @Override
    public void close(Player player, boolean save) {

        player.closeInventory();
        if (save) save();
    }

    @Override
    public void closeAll() {

        closeAll(true);
    }

    @Override
    public void closeAll(boolean save) {

        for (HumanEntity player : getInventory().getViewers()) {
            player.closeInventory();
        }
        if (save) save();
    }

    @Override
    public void save() {

        EbeanServer database = RaidCraft.getDatabase(RaidCraftPlugin.class);
        ObjectStorage<ItemStack> objectStorage = new ItemStorage("PersistentInventory");
        // lets get the table reference from the database
        TPersistentInventory table = database.find(TPersistentInventory.class, getId());
        // delete all stored objects
        database.delete(table.getSlots());

        // save new inventory slots
        Set<TPersistentInventorySlot> slots = new HashSet<>(inventory.getSize());
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack == null || itemStack.getType() == Material.AIR) continue;

            int objId = objectStorage.storeObject(itemStack);
            TPersistentInventorySlot slot = new TPersistentInventorySlot();
            slot.setInventory(table);
            slot.setObjectId(objId);
            slot.setSlot(i);
            slots.add(slot);
            database.save(slot);
        }
        // save inventory stuff
        table.setSlots(slots);
        table.setTitle(getTitle());
        table.setSize(inventory.getSize());
        table.setLastUpdate(new Timestamp(System.currentTimeMillis()));
        database.update(table);
    }

    @Override
    public void delete() {

        closeAll(false);
        EbeanServer database = RaidCraft.getDatabase(RaidCraftPlugin.class);
        TPersistentInventory table = database.find(TPersistentInventory.class, getId());
        database.delete(table);
        RaidCraft.getComponent(InventoryManager.class).unloadInventory(this);
    }
}
