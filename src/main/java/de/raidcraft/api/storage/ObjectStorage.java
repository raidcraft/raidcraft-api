package de.raidcraft.api.storage;

/**
 * @author Silthus
 */
public interface ObjectStorage<T> {

    /**
     * Returns the name of the provider that stored the object in the database.
     * Can be a plugin, skill, or whatever. Is only used to identify the source in the database.
     *
     * @return name of the source that stored the object
     */
    public String getStorageSource();

    /**
     * Stores the given object into a database or any implementation.
     * Will return an unique id by which the object can be retrieved from the store.
     *
     * @param object to store
     *
     * @return unique id to retrieve stored object
     */
    public int storeObject(T object);

    /**
     * Gets the stored object with the given unique id from the store.
     *
     * @param id of the stored object
     *
     * @return stored object
     *
     * @throws StorageException is thrown when the id is not found in the store
     */
    public T getObject(int id) throws StorageException;

    /**
     * Removes the given object from the store. Returning the stored object.
     *
     * @param id of the stored object
     *
     * @return removed object
     *
     * @throws StorageException is thrown when the id is not found in the store
     */
    public T removeObject(int id) throws StorageException;

    /**
     * Checks if the given object id is available in the store. Can be used to avoid exception handling of
     * the removeObject and getObject methods.
     *
     * @param id of the stored object.
     *
     * @return true if object is available in the store
     */
    public boolean isStored(int id);
}
