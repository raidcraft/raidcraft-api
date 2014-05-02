package de.raidcraft.api.action.action;

import de.raidcraft.api.action.ReflectionUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.function.Consumer;

/**
 * @author Silthus
 */
public interface Action<T> extends Consumer<T> {

    public default ConfigurationSection getConfig() {

        return new MemoryConfiguration();
    }

    public default boolean matchesType(Class<?> entity) {

        return ReflectionUtil.genericClassMatchesType(getClass(), entity);
    }
}
