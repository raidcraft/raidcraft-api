package de.raidcraft.api.conversations.answer;

import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.util.fanciful.FancyMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class ConfiguredAnswer extends SimpleAnswer {

    public ConfiguredAnswer(String type, ConfigurationSection config) {

        super(type, config.getString("text"), ActionAPI.createActions(config.getConfigurationSection("actions")),
                ActionAPI.createRequirements(config.getName(), config.getConfigurationSection("requirements")));
        load(config.getConfigurationSection("args"));
    }

    public ConfiguredAnswer(String type, String text, List<Action<?>> actions, List<Requirement<?>> requirements) {
        super(type, text, actions, requirements);
    }

    public ConfiguredAnswer(String text) {
        super(text);
    }

    public ConfiguredAnswer(String type, FancyMessage message, List<Action<?>> actions,
            List<Requirement<?>> requirements) {
        super(type, message, actions, requirements);
    }

    public ConfiguredAnswer(FancyMessage message) {
        super(message);
    }

    protected abstract void load(ConfigurationSection args);
}
