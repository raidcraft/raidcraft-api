package de.raidcraft.api.action.requirement;

import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.GenericType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

/**
 * @author mdoering
 */
@FunctionalInterface
public interface Requirement<T> extends GenericType<T>, RequirementConfigGenerator {

    public default String getIdentifier() {

        return ActionAPI.getIdentifier(this);
    }

    public default boolean test(T type) {

        return test(type, new MemoryConfiguration());
    }

    public boolean test(T type, ConfigurationSection config);

    public default boolean isChecked(T entity) {

        return false;
    }

    public default void setChecked(T entity, boolean checked) {

    }

    public default boolean isOrdered() {

        return false;
    }

    public default boolean isOptional() {

        return false;
    }

    public default void save() {

        throw new UnsupportedOperationException();
    }

    public default void load() {

        throw new UnsupportedOperationException();
    }

    public default void delete(T entity) {

        throw new UnsupportedOperationException();
    }
}
