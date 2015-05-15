package de.raidcraft.api.action.action;

import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.requirement.Requirement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author mdoering
 */
public interface ActionHolder {

    Collection<Action<?>> getActions();

    /**
     * Gets all actions and filters them to be applicable by the provided entity type.
     *
     * @param entityClazz to filter for
     *
     * @return filtered list
     */
    @SuppressWarnings("unchecked")
    default <T> Collection<Action<T>> getActions(Class<?> entityClazz) {

        return getActions().parallelStream()
                .filter(action -> ActionAPI.matchesType(action, entityClazz))
                .map(action -> (Action<T>) action)
                .collect(Collectors.toList());
    }

    /**
     * Gets all actions and applies the given filter to them before returning.
     *
     * @param filter to apply
     *
     * @return filtered list
     */
    default <T> Collection<Action<T>> getActions(Class<T> entityClazz, Predicate<? super Action<T>> filter) {

        Collection<Action<T>> actions = getActions(entityClazz);
        return actions.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }
}
