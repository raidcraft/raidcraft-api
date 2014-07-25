package de.raidcraft.api.action.requirement;

import de.raidcraft.api.action.ReflectionUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.lang.reflect.Method;

/**
 * @author mdoering
 */
@FunctionalInterface
public interface Requirement<T> extends RequirementConfigGenerator {

    public default boolean test(T type) {

        return test(type, new MemoryConfiguration());
    }

    public boolean test(T type, ConfigurationSection config);

    public default boolean matchesType(Class<?> entity) {

        for (Method method : getClass().getDeclaredMethods()) {
            if (method.getName().equals("test")) {
                return ReflectionUtil.isMatchingGenericMethodType(method, entity);
            }
        }
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
