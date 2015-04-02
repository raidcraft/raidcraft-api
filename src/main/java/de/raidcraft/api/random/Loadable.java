package de.raidcraft.api.random;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author mdoering
 */
public interface Loadable {

    public void load(ConfigurationSection config);
}
