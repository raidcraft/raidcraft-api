package de.raidcraft.api.conversations.answer;

import de.raidcraft.api.action.ActionAPI;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class ConfiguredAnswer extends SimpleAnswer {

    public ConfiguredAnswer(String type, ConfigurationSection config) {

        super(type, config.getString("withText"),
                ActionAPI.createActions(config.getConfigurationSection("actions")),
                ActionAPI.createRequirements(config.getName(), config.getConfigurationSection("withRequirement")));
        load(config.getConfigurationSection("args"));
    }

    protected abstract void load(ConfigurationSection args);
}
