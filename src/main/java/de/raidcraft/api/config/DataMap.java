package de.raidcraft.api.config;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.Map;

/**
 * @author Silthus
 */
public abstract class DataMap extends MemoryConfiguration {

    public DataMap(Map<?, ?> data) {

        super();
        convertMapsToSections(data, this);
    }

    public DataMap(ConfigurationSection data) {

        super();
        convertMapsToSections(data.getValues(true), this);
    }

    protected void convertMapsToSections(Map<?, ?> input, ConfigurationSection section) {

        for (Map.Entry<?, ?> entry : input.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();

            if (value instanceof Map) {
                convertMapsToSections((Map<?, ?>) value, section.createSection(key));
            } else {
                section.set(key, value);
            }
        }
    }

    /**
     * Will merge the given map with this map. The given map
     * will override values if defined.
     * You also need to make sure that the sections match up with the defined keys.
     *
     * @param section to merge
     */
    public void merge(ConfigurationSection section) {

        // we want to merge so that this current map gets overriden
        for (Map.Entry<String, Object> entry : section.getValues(true).entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
    }

    public ConfigurationSection getSafeConfigSection(String path) {

        ConfigurationSection configurationSection = getConfigurationSection(path);
        if (configurationSection == null) {
            configurationSection = createSection(path);
            save();
        }
        return configurationSection;
    }

    @Override
    public Object get(String path, Object def) {

        if (!isSet(path)) {
            set(path, def);
            save();
            return def;
        } else {
            return super.get(path, def);
        }
    }

    @Override
    public boolean isSet(String path) {

        Configuration root = getRoot();
        if (root == null) {
            return false;
        }
        if (root.options().copyDefaults()) {
            return contains(path);
        }
        return super.get(path, null) != null;
    }

    public abstract void save();
}
