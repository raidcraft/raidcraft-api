package de.raidcraft.api.action.requirement;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

/**
 * @author Silthus
 */
public interface Reasonable<T> {

    default String getReason(T entity) {

        return getReason(entity, new MemoryConfiguration());
    }

    String getReason(T entity, ConfigurationSection config);
}
