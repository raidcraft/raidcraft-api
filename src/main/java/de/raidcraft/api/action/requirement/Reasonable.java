package de.raidcraft.api.action.requirement;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public interface Reasonable<T> {

    public String getReason(T entity, ConfigurationSection config);
}
