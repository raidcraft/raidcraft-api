package de.raidcraft.api.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;

import java.util.Map;

/**
 * @author Silthus
 */
public abstract class DataMap extends MemorySection {

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

    public abstract void save();
}
