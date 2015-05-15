package de.raidcraft.api.action.action;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

/**
 * @author Silthus
 */
public interface RevertableAction<T> extends Action<T> {

    void revert(T type, ConfigurationSection config);

    default void revert(T type) {

        accept(type, new MemoryConfiguration());
    }
}

