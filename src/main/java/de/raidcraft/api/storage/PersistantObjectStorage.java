package de.raidcraft.api.storage;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;

/**
 * @author Silthus
 */
public abstract class PersistantObjectStorage<T> implements ObjectStorage<T> {

    private final String storageName;

    public PersistantObjectStorage(String storageName) {

        this.storageName = storageName;
    }

    @Override
    public final String getStorageName() {

        return storageName;
    }

    protected int store(String byteStream) {

        TObjectStorage storage = new TObjectStorage();
        storage.setStorageName(getStorageName());
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
