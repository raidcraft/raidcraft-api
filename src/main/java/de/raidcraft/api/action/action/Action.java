package de.raidcraft.api.action.action;

import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.GenericType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

/**
 * @author Silthus
 */
@FunctionalInterface
public interface Action<T> extends GenericType<T>, ActionConfigGenerator {

    public default String getIdentifier() {

        return ActionAPI.getIdentifier(this);
    }

    public void accept(T type, ConfigurationSection config);

    public default void accept(T type) {

        accept(type, new MemoryConfiguration());
    }
}
