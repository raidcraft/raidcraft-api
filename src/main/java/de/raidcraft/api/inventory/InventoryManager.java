package de.raidcraft.api.inventory;

import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.Component;

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

    public PersistentInventory getInventory(int id) {

        if (!loadedInventories.containsKey(id)) {
            SQLPersistentInventory inventory = new SQLPersistentInventory(id);
            loadedInventories.put(inventory.getId(), inventory);
        }
        return loadedInventories.get(id);
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
