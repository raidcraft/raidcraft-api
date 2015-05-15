package de.raidcraft.api.random;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author mdoering
 */
public interface Loadable {

    void load(ConfigurationSection config);
}
