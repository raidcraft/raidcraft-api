package de.raidcraft.api.storage;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import io.ebean.EbeanServer;

/**
 * @author Silthus
 */
public abstract class PersistantObjectStorage<T> implements ObjectStorage<T> {

    private final String storageSource;

    public PersistantObjectStorage(String storageSource) {

        this.storageSource = storageSource;
    }

    @Override
    public final String getStorageSource() {

        return storageSource;
    }

    protected int store(String byteStream) {

        TObjectStorage storage = new TObjectStorage();
        storage.setStorageName(getStorageSource());
        storage.setSerialization(byteStream);
        RaidCraft.getDatabase(RaidCraftPlugin.class).save(storage);
        return storage.getId();
    }

    protected String unstore(int id) throws StorageException {

        TObjectStorage storage = RaidCraft.getDatabase(RaidCraftPlugin.class).find(TObjectStorage.class, id);
        if (storage != null) {
            return storage.getSerialization();
        }
        throw new StorageException("No object with the id " + id + " is stored in the database.");
    }

    protected String remove(int id) throws StorageException {

        EbeanServer database = RaidCraft.getDatabase(RaidCraftPlugin.class);
        TObjectStorage storage = database.find(TObjectStorage.class, id);
        if (storage != null) {
            database.delete(storage);
            return storage.getSerialization();
        }
        throw new StorageException("No object with the id " + id + " is stored in the database.");
    }

    @Override
    public boolean isStored(int id) {

        return RaidCraft.getDatabase(RaidCraftPlugin.class).find(TObjectStorage.class, id) != null;
    }
}
