package de.raidcraft.api.storage;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            if (item != null && item.getType() != Material.AIR) {
                map.put(i, itemStorage.storeObject(item));
            } else {
                map.put(i, null);
            }
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream stream = new ObjectOutputStream(outputStream);
            stream.writeObject(map);
            int store = store(outputStream.toString("UTF-8"));
            stream.close();
            outputStream.close();
            return store;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
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

    @SuppressWarnings("unchecked")
    private Map<Integer, Integer> deserializeInventoryMap(String bytes) {

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes.getBytes());
            ObjectInputStream stream = new ObjectInputStream(inputStream);
            Map<Integer, Integer> map = (Map<Integer, Integer>) stream.readObject();
            inputStream.close();
            stream.close();
            return map;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
}
