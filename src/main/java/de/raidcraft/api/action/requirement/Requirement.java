package de.raidcraft.api.action.requirement;

import de.raidcraft.api.action.ReflectionUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.function.Predicate;

/**
 * @author mdoering
 */
@FunctionalInterface
public interface Requirement<T> extends Predicate<T> {

    public default ConfigurationSection getConfig() {

        return new MemoryConfiguration();
    }

    public default boolean matchesType(Class<?> entity) {

        return ReflectionUtil.genericClassMatchesType(getClass(), entity);
    }
}
