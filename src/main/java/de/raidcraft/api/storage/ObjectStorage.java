package de.raidcraft.api.storage;

/**
 * @author Silthus
 */
public interface ObjectStorage<T> {

    public String getStorageName();

    public int storeObject(T object);

    public T getObject(int id) throws StorageException;

    public T removeObject(int id) throws StorageException;

    public boolean isStored(int id);
}
