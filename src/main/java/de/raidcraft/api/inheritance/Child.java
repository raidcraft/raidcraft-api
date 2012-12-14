package de.raidcraft.api.inheritance;

import java.util.Set;

/**
 * @author Silthus
 */
public interface Child<T extends Parent> {

    public Set<T> getStrongParents();

    public Set<T> getWeakParents();

    public void addStrongParent(T parent);

    public void addWeakParent(T parent);

    public void removeStrongParent(T parent);

    public void removeWeakParent(T parent);
}
