package de.raidcraft.api.storage;

import de.raidcraft.util.SerializationUtil;
import org.bukkit.inventory.ItemStack;

/**
 * @author Silthus
 */
public class ItemStorage extends PersistantObjectStorage<ItemStack> {

    public ItemStorage(String storageSource) {

        super(storageSource);
    }

    @Override
    public int storeObject(ItemStack object) {

        if (object == null) return -1;

        return store(SerializationUtil.toByteStream(object));
    }

    @Override
    public ItemStack removeObject(int id) throws StorageException {

        return (ItemStack) SerializationUtil.fromByteStream(remove(id));
    }

    @Override
    public ItemStack getObject(int id) throws StorageException {

        return (ItemStack) SerializationUtil.fromByteStream(unstore(id));
    }
}
