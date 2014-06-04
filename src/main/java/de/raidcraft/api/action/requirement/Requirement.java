package de.raidcraft.api.action.requirement;

import de.raidcraft.api.action.ReflectionUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.lang.reflect.Method;
import java.util.function.Predicate;

/**
 * @author mdoering
 */
@FunctionalInterface
public interface Requirement<T> extends Predicate<T> {

    public default ConfigurationSection getConfig() {

        return new MemoryConfiguration();
    }

    public default boolean isPersistant() {

        return getConfig().getBoolean("persistant", false);
    }

    public default boolean isOrdered() {

        return getOrder() > 0;
    }

    public default int getOrder() {

        return getConfig().getInt("order", 0);
    }

    public default boolean isCounting() {

        return getRequiredCount() > 1;
    }

    public default int getRequiredCount() {

        return getConfig().getInt("count", 0);
    }

    public default int getCount() {

        return 0;
    }

    public default boolean isOptional() {

        return getConfig().getBoolean("optional", false);
    }

    public default boolean matchesType(Class<?> entity) {

        for (Method method : getClass().getDeclaredMethods()) {
            if (method.getName().equals("test")) {
                return ReflectionUtil.isMatchingGenericMethodType(method, entity);
            }
        }
        return false;
    }
}
