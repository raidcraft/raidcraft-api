package de.raidcraft.api.action.action;

import de.raidcraft.api.action.ActionAPI;

import java.util.Collection;
import java.util.List;
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
    default <T> Collection<Action<T>> getActions(Class<?> entityClazz) {

        return getFilteredActions(getActions(), entityClazz);
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

    default <T> Action<T> addAction(Action<T> action) {
        getActions().add(action);
        return action;
    }

    @SuppressWarnings("unchecked")
    static <T> List<Action<T>> getFilteredActions(Collection<Action<?>> actions, Class<?> entityClazz) {

        return actions.stream()
                .filter(action -> ActionAPI.matchesType(action, entityClazz))
                .map(action -> (Action<T>) action)
                .collect(Collectors.toList());
    }
}
