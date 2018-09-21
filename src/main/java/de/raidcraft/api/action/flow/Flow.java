package de.raidcraft.api.action.flow;

import de.raidcraft.api.action.TriggerFactory;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.stage.StageTemplate;
import de.raidcraft.util.ConfigUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

/**
 * @author mdoering
 */
public final class Flow {

    /**
     * Recurses the given config section in search for any flow groups or flow actions.
     *
     * @param config section to load actions from
     * @return list of loaded actions
     */
    public static List<Action<?>> parseActions(ConfigurationSection config) {
        return new FlowConfigParser(config).parseActions();
    }

    public static List<TriggerFactory> parseTrigger(ConfigurationSection config) {

        return new FlowConfigParser(config).parseTrigger();
    }

    public static List<Requirement<?>> parseRequirements(String id, ConfigurationSection config) {
        return new FlowConfigParser(config).parseRequirements(id);
    }

    public static List<Requirement<?>> parseRequirements(ConfigurationSection config) {

        return new FlowConfigParser(config).parseRequirements(ConfigUtil.getFileName(config));
    }

    public static List<Answer> parseAnswers(StageTemplate template, ConfigurationSection config) {

        return new FlowConfigParser(config).parseAnswers(template);
    }
}
