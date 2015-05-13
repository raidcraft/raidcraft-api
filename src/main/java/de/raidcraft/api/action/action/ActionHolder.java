package de.raidcraft.api.action.action;

import de.raidcraft.api.action.requirement.Requirement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author mdoering
 */
public interface ActionHolder {

    public Collection<Action<?>> getActions();

    /**
     * Gets all actions and filters them to be applicable by the provided entity type.
     *
     * @param entityClazz to filter for
     *
     * @return filtered list
     */
    @SuppressWarnings("unchecked")
    public default <T> Collection<Requirement<T>> getActions(Class<?> entityClazz) {

        return getActions().parallelStream()
                .filter(requirement -> requirement.matchesType(entityClazz))
                .map(requirement -> (Requirement<T>) requirement)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Gets all actions and applies the given filter to them before returning.
     *
     * @param filter to apply
     *
     * @return filtered list
     */
    public default <T> Collection<Requirement<T>> getActions(Class<T> entityClazz, Predicate<? super Requirement<T>> filter) {

        Collection<Requirement<T>> requirements = getActions(entityClazz);
        return requirements.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }
}
