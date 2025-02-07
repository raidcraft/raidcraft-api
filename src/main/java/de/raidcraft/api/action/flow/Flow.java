package de.raidcraft.api.action.flow;

import de.raidcraft.api.action.RequirementConfigWrapper;
import de.raidcraft.api.action.TriggerFactory;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.stage.StageTemplate;
import de.raidcraft.util.ConfigUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
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
        if (config == null) return new ArrayList<>();
        return new FlowConfigParser(config).parseActions();
    }

    public static List<TriggerFactory> parseTrigger(ConfigurationSection config) {
        if (config == null) return new ArrayList<>();
        return new FlowConfigParser(config).parseTrigger();
    }

    public static List<RequirementConfigWrapper<?>> parseRequirements(String id, ConfigurationSection config) {
        if (config == null) return new ArrayList<>();
        return new FlowConfigParser(config).parseRequirements(id);
    }

    public static List<RequirementConfigWrapper<?>> parseRequirements(ConfigurationSection config) {
        if (config == null) return new ArrayList<>();
        return new FlowConfigParser(config).parseRequirements(ConfigUtil.getFileName(config));
    }

    public static List<Answer> parseAnswers(StageTemplate template, ConfigurationSection config) {
        if (config == null || template == null) return new ArrayList<>();
        return new FlowConfigParser(config).parseAnswers(template);
    }
}
