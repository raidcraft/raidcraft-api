package de.raidcraft.api.action.requirement;

import de.raidcraft.api.action.ActionAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

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

    default boolean isChecked(T entity) {

        return false;
    }

    default void setChecked(T entity, boolean checked) {

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
