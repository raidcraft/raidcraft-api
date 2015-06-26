package de.raidcraft.api.action.flow;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.flow.parsers.ActionTypeParser;
import de.raidcraft.api.action.flow.types.ActionAPIType;
import de.raidcraft.api.action.flow.types.FlowDelay;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.util.ConfigUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author mdoering
 */
public final class Flow {

    private static final FlowParser[] parsers = {
            new ActionTypeParser()
    };

    public static <T> List<Action<T>> parseActions(ConfigurationSection config, Class<T> type) {

        List<Action<T>> actions = new ArrayList<>();
        Set<String> keys = config.getKeys(false);
        if (keys == null) return actions;

        long delay = 0;
        for (String key : keys) {
            if (config.isList(key)) {
                try {
                    List<FlowExpression> flowExpressions = parse(config.getStringList(key));
                    // we are gonna add all requirements to this list until an action is added
                    // then when a requirement is added again the list will be reset
                    boolean resetRequirements = false;
                    List<Requirement<T>> applicableRequirements = new ArrayList<>();
                    for (FlowExpression flowExpression : flowExpressions) {
                        if (flowExpression instanceof FlowDelay) {
                            delay += ((FlowDelay) flowExpression).getDelay();
                            continue;
                        }
                        if (flowExpression instanceof ActionAPIType) {
                            ActionAPIType expression = (ActionAPIType) flowExpression;
                            switch (expression.getFlowType()) {
                                case ACTION:
                                    FlowConfiguration configuration = expression.getConfiguration();
                                    configuration.set("delay", delay);
                                    Optional<Action<T>> action = ActionAPI.createAction(expression.getId(), configuration, type);
                                    if (!action.isPresent()) {
                                        throw new FlowException("Could not find action of type " + expression.getTypeId());
                                    }
                                    actions.add(action.get());
                                    if (!applicableRequirements.isEmpty()) {
                                        applicableRequirements.forEach(action.get()::addRequirement);
                                        resetRequirements = true;
                                    }
                                    break;
                                case REQUIREMENT:
                                    Optional<Requirement<T>> requirement = ActionAPI.createRequirement(ConfigUtil.getFileName(config).replace("/", ".") + key,
                                            expression.getTypeId(),
                                            expression.getConfiguration(),
                                            type);
                                    if (!requirement.isPresent()) {
                                        throw new FlowException("Could not find requirement of type " + expression.getTypeId());
                                    }
                                    // not sure if we want to reset or not, needs to be discussed
                                    // if (resetRequirements) applicableRequirements.clear();
                                    applicableRequirements.add(requirement.get());
                                    break;
                            }
                        }
                    }
                } catch (FlowException e) {
                    RaidCraft.LOGGER.warning("Error when parsing " + key + " inside " + ConfigUtil.getFileName(config) + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return actions;
    }

    public static List<FlowExpression> parse(List<String> lines) throws FlowException {

        List<FlowExpression> expressions = new ArrayList<>();

        for (String line : lines) {
            for (FlowParser parser : parsers) {
                if (parser.accept(line)) {
                    expressions.add(parser.parse());
                }
            }
        }
        return expressions;
    }
}
