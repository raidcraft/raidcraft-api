package de.raidcraft.api.inventory;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.Component;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Philip Urban
 */
public class InventoryManager implements Component {

    private final RaidCraftPlugin plugin;
    private Map<Integer, PersistentInventory> loadedInventories = new HashMap<>();

    public InventoryManager(RaidCraftPlugin plugin) {

        this.plugin = plugin;
        plugin.registerEvents(new InventoryListener());
    }

    public PersistentInventory getInventory(int id) throws InvalidInventoryException {

        if (!loadedInventories.containsKey(id)) {
            if(RaidCraft.getDatabase(RaidCraftPlugin.class).find(TPersistentInventory.class, id) == null) {
                throw new InvalidInventoryException("Inventory with id " + id + " doesn't exists!");
            }
            SQLPersistentInventory inventory = new SQLPersistentInventory(id);
            loadedInventories.put(inventory.getId(), inventory);
        }
        return loadedInventories.get(id);
    }

    public PersistentInventory createInventory(Inventory inventory) {

        return new SQLPersistentInventory(inventory);
    }

    public PersistentInventory createInventory(String title, int size) {

        return new SQLPersistentInventory(title, size);
    }

    public void loadInventory(PersistentInventory inventory) {

        if (loadedInventories.containsKey(inventory.getId())) {
            return;
        }
        loadedInventories.put(inventory.getId(), inventory);
    }

    public PersistentInventory unloadInventory(PersistentInventory inventory) {

        return loadedInventories.remove(inventory.getId());
    }

    public List<PersistentInventory> getLoadedInventories() {

        return new ArrayList<>(loadedInventories.values());
    }
}
