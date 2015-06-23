package de.raidcraft.api.action.requirement;

import de.raidcraft.api.action.ActionAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

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

    default int getIntMapping(T entity, String key) {

        String mapping = getMapping(entity, key);
        return mapping == null ? 0 : Integer.parseInt(mapping);
    }

    default boolean getBoolMapping(T entity, String key) {

        String mapping = getMapping(entity, key);
        return mapping != null && Boolean.parseBoolean(mapping);
    }

    default double getDoubleMapping(T entity, String key) {

        String mapping = getMapping(entity, key);
        return mapping == null ? 0.0 : Double.parseDouble(mapping);
    }

    default String getMapping(T entity, String key) {

        throw new UnsupportedOperationException();
    }

    default void setMapping(UUID uuid, String key, String value) {

        throw new UnsupportedOperationException();
    }

    default void setMapping(T entity, String key, String value) {

        if (entity instanceof Player) {
            UUID uniqueId = ((Player) entity).getUniqueId();
            setMapping(uniqueId, key, value);
        }
    }

    default void setMapping(T entity, String key, int value) {

        setMapping(entity, key, Integer.toString(value));
    }

    default void setMapping(T entity, String key, boolean value) {

        setMapping(entity, key, Boolean.toString(value));
    }

    default boolean isMapped(T entity, String key) {

        return getMapping(entity, key) != null;
    }

    default boolean isChecked(T entity) {

        return false;
    }

    default void setChecked(T entity, boolean checked) {

    }

    default Optional<String> getDescription(T entity) {

        return getDescription(entity, new MemoryConfiguration());
    }

    default Optional<String> getDescription(T entity, ConfigurationSection config) {

        return Optional.empty();
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
