package de.raidcraft.api.storage;

import de.raidcraft.util.SerializationUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mdoering
 */
public class InventoryStorage extends PersistantObjectStorage<ItemStack[]> {

    private final ItemStorage itemStorage;

    public InventoryStorage(String storageSource) {

        super(storageSource);
        itemStorage = new ItemStorage(storageSource);
    }

    @Override
    public int storeObject(ItemStack[] items) {

        HashMap<String, Object> map = new HashMap<>();
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            if (item != null && item.getType() != Material.AIR) {
                map.put(i + "", itemStorage.storeObject(item));
            } else {
                map.put(i + "", itemStorage.storeObject(new ItemStack(Material.AIR)));
            }
        }
        String bytes = SerializationUtil.toByteStream(map);
        return store(bytes);
    }

    @Override
    public ItemStack[] getObject(int id) throws StorageException {

        return getInventory(deserializeInventoryMap(unstore(id)));
    }

    @Override
    public ItemStack[] removeObject(int id) throws StorageException {

        return getInventory(deserializeInventoryMap(remove(id)));
    }

    private ItemStack[] getInventory(Map<Integer, Integer> map) {

        ItemStack[] itemStacks = new ItemStack[map.size()];
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            try {
                if (entry.getValue() == null) {
                    itemStacks[entry.getKey()] = null;
                } else {
                    ItemStack itemStack = itemStorage.getObject(entry.getValue());
                    itemStacks[entry.getKey()] = itemStack;
                }
            } catch (StorageException e) {
                e.printStackTrace();
            }
        }
        return itemStacks;
    }

    private Map<Integer, Integer> deserializeInventoryMap(String bytes) {

        Map<Integer, Integer> inventoryMap = new HashMap<>();
        // key is an int that holds the slot id and the object is also an int with the storage id
        Map<String, Object> slotStorageIdMap = SerializationUtil.mapFromByteStream(bytes);
        for (Map.Entry<String, Object> entry : slotStorageIdMap.entrySet()) {
            int slot = Integer.parseInt(entry.getKey());
            int storageId = (int) entry.getValue();
            inventoryMap.put(slot, storageId);
        }
        return inventoryMap;
    }
}
