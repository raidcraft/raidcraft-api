package de.raidcraft.api.action.flow;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.TriggerFactory;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.flow.parsers.ActionTypeParser;
import de.raidcraft.api.action.flow.parsers.AnswerParser;
import de.raidcraft.api.action.flow.types.ActionAPIType;
import de.raidcraft.api.action.flow.types.FlowAnswer;
import de.raidcraft.api.action.flow.types.FlowDelay;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.stage.StageTemplate;
import de.raidcraft.util.ConfigUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author mdoering
 */
public final class Flow {

    private static final FlowParser[] parsers = {
            new ActionTypeParser(),
            new AnswerParser()
    };

    public static List<Action<?>> parseActions(ConfigurationSection config) {

        List<Action<?>> actions = new ArrayList<>();
        if (config == null) return actions;
        Set<String> keys = config.getKeys(false);
        if (keys == null) return actions;

        for (String key : keys) {
            if (config.isList(key)) {
                try {
                    long delay = 0;
                    List<FlowExpression> flowExpressions = parse(config.getStringList(key));
                    // we are gonna add all requirements to this list until an action is added
                    boolean resetRequirements = false;
                    List<Requirement<?>> applicableRequirements = new ArrayList<>();
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
                                    Optional<Action<?>> action = ActionAPI.createAction(expression.getTypeId(), configuration);
                                    if (!action.isPresent()) {
                                        throw new FlowException("Could not find action of type " + expression.getTypeId());
                                    }
                                    actions.add(action.get());
                                    if (!applicableRequirements.isEmpty()) {
                                        applicableRequirements.forEach(requirement -> action.get().addRequirement(requirement));
                                        resetRequirements = true;
                                    }
                                    break;
                                case REQUIREMENT:
                                    Optional<Requirement<?>> requirement = ActionAPI.createRequirement(ConfigUtil.getFileName(config).replace("/", ".") + key,
                                            expression.getTypeId(),
                                            expression.getConfiguration());
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

    public static List<TriggerFactory> parseTrigger(ConfigurationSection config) {

        List<TriggerFactory> triggerFactories = new ArrayList<>();
        if (config == null) return triggerFactories;
        Set<String> keys = config.getKeys(false);
        if (keys == null) return triggerFactories;

        int i = 0;
        for (String key : keys) {
            if (config.isList(key)) {
                try {
                    long delay = 0;
                    String id = ConfigUtil.getFileName(config).replace("/", ".") + key;
                    List<FlowExpression> flowExpressions = parse(config.getStringList(key));
                    // we are gonna add all requirements to this list until an action is added
                    List<ActionAPIType> applicableRequirements = new ArrayList<>();
                    TriggerFactory activeTrigger = null;

                    for (FlowExpression flowExpression : flowExpressions) {
                        if (flowExpression instanceof FlowDelay) {
                            delay += ((FlowDelay) flowExpression).getDelay();
                            continue;
                        }
                        if (flowExpression instanceof ActionAPIType) {
                            ActionAPIType expression = (ActionAPIType) flowExpression;
                            FlowConfiguration configuration = expression.getConfiguration();
                            switch (expression.getFlowType()) {
                                case TRIGGER:
                                    if (!applicableRequirements.isEmpty()) {
                                        for (ActionAPIType requirement : applicableRequirements) {
                                            configuration.set("requirements.flow-" + i++, requirement.getConfiguration());
                                        }
                                    }
                                    applicableRequirements.clear();
                                    TriggerFactory trigger = ActionAPI.createTrigger(id, configuration);
                                    activeTrigger = trigger;
                                    triggerFactories.add(trigger);
                                    // reset the delay when a new trigger starts
                                    delay = 0;
                                    break;
                                case ACTION:
                                    if (activeTrigger != null) {
                                        String actionId = "actions.flow-" + i;
                                        configuration.set("delay", delay);
                                        if (!applicableRequirements.isEmpty()) {
                                            for (ActionAPIType requirement : applicableRequirements) {
                                                configuration.set(actionId + ".requirements.flow-" + i++, requirement.getConfiguration());
                                            }
                                        }
                                        activeTrigger.getConfig().set(actionId, configuration);
                                    }
                                    break;
                                case REQUIREMENT:
                                    applicableRequirements.add(expression);
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
        return triggerFactories;
    }

    public static List<Requirement<?>> parseRequirements(ConfigurationSection config) {

        List<Requirement<?>> requirements = new ArrayList<>();
        if (config == null) return requirements;
        Set<String> keys = config.getKeys(false);
        if (keys == null) return requirements;

        int i = 0;
        for (String key : keys) {
            if (config.isList(key)) {
                try {
                    long delay = 0;
                    List<FlowExpression> flowExpressions = parse(config.getStringList(key));
                    Requirement<?> activeRequirement = null;
                    for (FlowExpression flowExpression : flowExpressions) {
                        if (flowExpression instanceof FlowDelay) {
                            delay += ((FlowDelay) flowExpression).getDelay();
                            continue;
                        }
                        if (flowExpression instanceof ActionAPIType) {
                            ActionAPIType expression = (ActionAPIType) flowExpression;
                            FlowConfiguration configuration = expression.getConfiguration();
                            switch (expression.getFlowType()) {
                                case ACTION:
                                    if (activeRequirement == null) continue;
                                    configuration.set("delay", delay);
                                    Optional<Action<?>> action = ActionAPI.createAction(expression.getTypeId(), configuration);
                                    if (!action.isPresent()) {
                                        throw new FlowException("Could not find action of type " + expression.getTypeId());
                                    }
                                    activeRequirement.addAction(action.get());
                                    break;
                                case REQUIREMENT:
                                    Optional<Requirement<?>> requirement = ActionAPI.createRequirement(ConfigUtil.getFileName(config).replace("/", ".") + key,
                                            expression.getTypeId(),
                                            expression.getConfiguration());
                                    if (!requirement.isPresent()) {
                                        throw new FlowException("Could not find requirement of type " + expression.getTypeId());
                                    }
                                    requirements.add(requirement.get());
                                    activeRequirement = requirement.get();
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
        return requirements;
    }

    @SuppressWarnings("unchecked")
    public static List<Answer> parseAnswers(StageTemplate template, ConfigurationSection config) {

        List<Answer> answers = new ArrayList<>();
        if (config == null) return answers;
        Set<String> keys = config.getKeys(false);
        if (keys == null) return answers;

        for (String key : keys) {
            if (config.isList(key)) {
                try {
                    long delay = 0;
                    List<FlowExpression> flowExpressions = parse(config.getStringList(key));
                    List<Requirement<Player>> playerRequirements = new ArrayList<>();
                    List<Requirement<Conversation>> conversationRequirements = new ArrayList<>();
                    Answer activeAnswer = null;

                    for (FlowExpression flowExpression : flowExpressions) {
                        if (flowExpression instanceof FlowDelay) {
                            delay += ((FlowDelay) flowExpression).getDelay();
                        } else if (flowExpression instanceof FlowAnswer) {
                            Optional<Answer> answer = ((FlowAnswer) flowExpression).create(template);
                            if (!answer.isPresent()) {
                                throw new FlowException("Could not create answer (type not found?)");
                            }
                            answers.add(answer.get());
                            activeAnswer = answer.get();
                            if (!playerRequirements.isEmpty()) {
                                playerRequirements.forEach(activeAnswer::addPlayerRequirement);
                                playerRequirements.clear();
                            }
                            if (!conversationRequirements.isEmpty()) {
                                conversationRequirements.forEach(activeAnswer::addConversationRequirement);
                            }
                        } else if (flowExpression instanceof ActionAPIType) {
                            ActionAPIType expression = (ActionAPIType) flowExpression;
                            FlowConfiguration configuration = expression.getConfiguration();
                            switch (expression.getFlowType()) {
                                case ACTION:
                                    if (activeAnswer == null) continue;
                                    configuration.set("delay", delay);
                                    Optional<Action<Conversation>> convAction = ActionAPI.createAction(expression.getTypeId(), configuration, Conversation.class);
                                    if (convAction.isPresent()) {
                                        Action<Conversation> action = convAction.get();
                                        activeAnswer.addConversationAction(action);
                                        if (!conversationRequirements.isEmpty()) {
                                            conversationRequirements.forEach(action::addRequirement);
                                        }
                                    } else {
                                        Optional<Action<Player>> playerAction = ActionAPI.createAction(expression.getTypeId(), configuration, Player.class);
                                        if (playerAction.isPresent()) {
                                            Action<Player> action = playerAction.get();
                                            activeAnswer.addPlayerAction(action);
                                            if (!playerRequirements.isEmpty()) {
                                                playerRequirements.forEach(action::addRequirement);
                                            }
                                        }
                                    }
                                    break;
                                case REQUIREMENT:
                                    Optional<Requirement<Player>> playerRequirement = ActionAPI.createRequirement(
                                            ConfigUtil.getFileName(config).replace("/", ".") + key,
                                            expression.getTypeId(),
                                            expression.getConfiguration(),
                                            Player.class);
                                    if (!playerRequirement.isPresent()) {
                                        Optional<Requirement<Conversation>> conversationRequirement = ActionAPI.createRequirement(
                                                ConfigUtil.getFileName(config).replace("/", ".") + key,
                                                expression.getTypeId(),
                                                expression.getConfiguration(),
                                                Conversation.class);
                                        if (conversationRequirement.isPresent()) {
                                            throw new FlowException("Could not find valid requirement type for " + expression.getTypeId());
                                        }
                                        conversationRequirements.add(conversationRequirement.get());
                                    } else {
                                        playerRequirements.add(playerRequirement.get());
                                    }
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
        return answers;
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
