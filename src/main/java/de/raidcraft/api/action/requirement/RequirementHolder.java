package de.raidcraft.api.action.requirement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author mdoering
 */
public interface RequirementHolder {

    public Collection<Requirement<?>> getRequirements();

    public default <T> boolean isMeetingAllRequirements(T entity) {

        Collection<Requirement<T>> requirements = getRequirements(entity.getClass());
        return requirements.stream()
                .allMatch(requirement -> requirement.test(entity));
    }

    /**
     * Gets all requirements and filters them to be applicable by the provided entity type.
     *
     * @param entityClazz to filter for
     *
     * @return filtered list
     */
    @SuppressWarnings("unchecked")
    public default <T> Collection<Requirement<T>> getRequirements(Class<?> entityClazz) {

        return getRequirements().parallelStream()
                .filter(requirement -> requirement.matchesType(entityClazz))
                .map(requirement -> (Requirement<T>) requirement)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Gets all requirements and applies the given filter to them before returning.
     *
     * @param filter to apply
     *
     * @return filtered list
     */
    public default <T> Collection<Requirement<T>> getRequirements(Class<T> entityClazz, Predicate<? super Requirement<T>> filter) {

        Collection<Requirement<T>> requirements = getRequirements(entityClazz);
        return requirements.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }
}
