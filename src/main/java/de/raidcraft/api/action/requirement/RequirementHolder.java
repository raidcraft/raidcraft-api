package de.raidcraft.api.action.requirement;

import de.raidcraft.api.action.ActionAPI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author mdoering
 */
public interface RequirementHolder {

    Collection<Requirement<?>> getRequirements();

    default <T> boolean isMeetingAllRequirements(T entity) {

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
    default <T> Collection<Requirement<T>> getRequirements(Class<?> entityClazz) {

        return getRequirements().parallelStream()
                .filter(requirement -> ActionAPI.matchesType(requirement, entityClazz))
                .map(requirement -> (Requirement<T>) requirement)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    default <T> Requirement<T> addRequirement(Requirement<T> requirement) {
        getRequirements().add(requirement);
        return requirement;
    }

    /**
     * Gets all requirements and applies the given filter to them before returning.
     *
     * @param filter to apply
     *
     * @return filtered list
     */
    default <T> Collection<Requirement<T>> getRequirements(Class<T> entityClazz, Predicate<? super Requirement<T>> filter) {

        Collection<Requirement<T>> requirements = getRequirements(entityClazz);
        return requirements.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }
}
