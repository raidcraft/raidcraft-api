package de.raidcraft.api.inventory;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.Component;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip Urban
 */
public class InventoryManager implements Component {

    private final RaidCraftPlugin plugin;

    public InventoryManager(RaidCraftPlugin plugin) {
        this.plugin = plugin;
        plugin.registerEvents(new InventoryListener());
    }

    public PersistentInventory getInventory(int id) throws InvalidInventoryException {
        if (RaidCraft.getDatabase(RaidCraftPlugin.class).find(TPersistentInventory.class, id) == null) {
            throw new InvalidInventoryException("Inventory with id " + id + " doesn't exists!");
        }
        return new SQLPersistentInventory(id);
    }

    public PersistentInventory createInventory(Inventory inventory) {
        return new SQLPersistentInventory(inventory);
    }

    public PersistentInventory createInventory(String title, int size) {
        return new SQLPersistentInventory(title, size);
    }

    @Deprecated
    public void loadInventory(PersistentInventory inventory) {
        // nothing
    }

    @Deprecated
    public PersistentInventory unloadInventory(PersistentInventory inventory) {
        return inventory;
    }

    @Deprecated
    public List<PersistentInventory> getLoadedInventories() {
        return new ArrayList<>();
    }
}
