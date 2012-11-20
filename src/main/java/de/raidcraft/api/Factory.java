package de.raidcraft.api;

/**
 * @author Silthus
 */
public interface Factory<T,K> {

    public T load(K id);
}
