package de.raidcraft.api.action.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author mdoering
 */
public interface ActionHolder<T> {

    public Collection<Action<?>> getActions();

    /**
     * Gets all requirements and filters them to be applicable by the provided entity type.
     *
     * @param entityClazz to filter for
     *
     * @return filtered list
     */
    @SuppressWarnings("unchecked")
    public default Collection<Action<T>> getActions(Class<?> entityClazz) {

        return getActions().parallelStream()
                .filter(action -> action.matchesType(entityClazz))
                .map(action -> (Action<T>) action)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Gets all requirements and applies the given filter to them before returning.
     *
     * @param filter to apply
     *
     * @return filtered list
     */
    public default Collection<Action<T>> getActions(Class<?> entityClazz, Predicate<? super Action<T>> filter) {

        return getActions(entityClazz).stream()
                .filter(filter)
                .collect(Collectors.toList());
    }
}
