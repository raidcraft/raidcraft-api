package de.raidcraft.api.inheritance;

import java.util.Collection;

/**
 * @author Silthus
 */
public interface Child<T extends Parent> {

    public Collection<T> getStrongParents();

    public Collection<T> getWeaksParents();

    public void addStrongParent(T parent);

    public void addWeakParent(T parent);

    public void removeStrongParent(T parent);

    public void removeWeakParent(T parent);
}
