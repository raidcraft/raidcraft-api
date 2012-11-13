package de.raidcraft.api.inheritance;

/**
 * @author Silthus
 */
public interface Child<T extends Parent> {

    public T getParent();
}
