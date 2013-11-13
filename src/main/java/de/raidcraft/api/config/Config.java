package de.raidcraft.api.config;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public interface Config extends ConfigurationSection {

    public void reload();

    public void load();

    public void save();
}
