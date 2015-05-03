package de.raidcraft.api.action.requirement;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public interface Reasonable<T> {

    public String getShortReason(T entity, ConfigurationSection config);

    public String getLongReason(T entity, ConfigurationSection config);
}
