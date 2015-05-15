package de.raidcraft.api.action.action;

import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.ActionAPIConfigGenerator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

/**
 * @author mdoering
 */
public interface ActionConfigGenerator extends ActionAPIConfigGenerator {

    default ConfigurationSection createConfigSection() {

        MemoryConfiguration config = new MemoryConfiguration();
        config.set("type", ActionAPI.getIdentifier(this));
        return config.createSection("args");
    }

    @Override
    default String getPath() {

        return "actions";
    }
}
