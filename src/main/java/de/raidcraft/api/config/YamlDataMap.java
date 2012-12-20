package de.raidcraft.api.config;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class YamlDataMap extends DataMap {

    private final ConfigurationBase config;

    public YamlDataMap(ConfigurationSection data, ConfigurationBase config) {

        super(data);
        this.config = config;
    }

    @Override
    public void save() {

        config.set(getCurrentPath(), getValues(true));
        config.save();
    }
}
