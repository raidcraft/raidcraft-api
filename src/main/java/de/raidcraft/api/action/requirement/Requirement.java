package de.raidcraft.api.action.requirement;

import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.action.Action;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.Optional;

/**
 * @author mdoering
 */
@FunctionalInterface
public interface Requirement<T> extends RequirementConfigGenerator {

    default String getIdentifier() {

        return ActionAPI.getIdentifier(this);
    }

    default boolean test(T type) {

        return test(type, new MemoryConfiguration());
    }

    boolean test(T type, ConfigurationSection config);

    default void addAction(Action<?> action) {

        throw new UnsupportedOperationException();
    }

    default void addFailureAction(Action<?> action) {
        throw new UnsupportedOperationException();
    }

    default Optional<String> getDescription(T entity) {

        return getDescription(entity, new MemoryConfiguration());
    }

    default Optional<String> getDescription(T entity, ConfigurationSection config) {

        return Optional.empty();
    }

    default Requirement<T> with(String key, Object value) {

        throw new UnsupportedOperationException();
    }

    default Requirement<T> withArgs(String key, Object value) {

        throw new UnsupportedOperationException();
    }

    default boolean isOrdered() {

        return false;
    }

    default boolean isOptional() {

        return false;
    }

    default void save() {

        throw new UnsupportedOperationException();
    }

    default void load() {

        throw new UnsupportedOperationException();
    }

    default void delete(T entity) {

        throw new UnsupportedOperationException();
    }
}
