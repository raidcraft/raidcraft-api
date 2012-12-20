package de.raidcraft.api.config;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class YamlDataMap extends DataMap {

    private final ConfigurationBase config;
    private final String path;

    public YamlDataMap(ConfigurationSection data, ConfigurationBase config) {

        super(data);
        this.config = config;
        this.path = data.getCurrentPath();
    }

    @Override
    public void save() {

        config.set(path, getValues(true));
        config.save();
    }
}
