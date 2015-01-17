package de.raidcraft.api.action.action;

import de.raidcraft.api.action.ReflectionUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.lang.reflect.Method;

/**
 * @author Silthus
 */
@FunctionalInterface
public interface Action<T> extends ActionConfigGenerator {

    public default boolean matchesType(Class<?> entity) {

        for (Method method : getClass().getDeclaredMethods()) {
            if (method.getName().equals("accept")) {
                return ReflectionUtil.isMatchingGenericMethodType(method, entity);
            }
        }
        return false;
    }

    public void accept(T type, ConfigurationSection config);

    public default void accept(T type) {

        accept(type, new MemoryConfiguration());
    }
}
