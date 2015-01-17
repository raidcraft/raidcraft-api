package de.raidcraft.api.action.trigger;

import de.raidcraft.api.action.ActionAPIConfigGenerator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

/**
 * @author mdoering
 */
public interface TriggerConfigGenerator extends ActionAPIConfigGenerator {

    @Override
    public default ConfigurationSection createConfigSection() {

        throw new UnsupportedOperationException("Please use the createConfigSection(Information.class) method for trigger!");
    }

    public default ConfigurationSection createConfigSection(Information information) {

        MemoryConfiguration config = new MemoryConfiguration();
        config.set("type", information.value());
        return config.createSection("args");
    }

    @Override
    default String getPath() {

        return "trigger";
    }
}
