package de.raidcraft.api.action.flow;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.TriggerFactory;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.GroupAction;
import de.raidcraft.api.action.flow.parsers.ActionApiFlowParser;
import de.raidcraft.api.action.flow.parsers.AnswerParser;
import de.raidcraft.api.action.flow.types.ActionAPIType;
import de.raidcraft.api.action.flow.types.FlowAlias;
import de.raidcraft.api.action.flow.types.FlowAnswer;
import de.raidcraft.api.action.flow.types.FlowDelay;
import de.raidcraft.api.action.requirement.GroupRequirement;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.stage.StageTemplate;
import de.raidcraft.util.ConfigUtil;
import javafx.util.Pair;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Parses a given config section for all flow statements and returns a list of actions, requirements or triggers.
 * Manages local variable groups for the parsing flow block.
 */
@Data
public class FlowConfigParser {

    private static final FlowParser[] parsers = {new ActionApiFlowParser(), new AnswerParser()};

    private final ConfigurationSection config;

    private final Map<String, FlowAlias> aliasMap = new HashMap<>();

    public FlowConfigParser(ConfigurationSection config) {
        this.config = config;
        getVariableGroupSection().ifPresent(this::loadVariableGroups);
    }

    /**
     * Parses the config section for any defined variable groupings
     * and registers them as local aliases.
     */
    private void loadVariableGroups(ConfigurationSection section) {

        ActionApiFlowParser parser = new ActionApiFlowParser();

        for (String key : section.getKeys(false)) {
            try {
                if (aliasMap.containsKey(key)) {
                    throw new FlowException("Duplicate alias group with key: " + key);
                } else if (section.isString(key)) {
                    if (parser.accept(section.getString(key))) {
                        aliasMap.put(key, new FlowAlias(key, parser.parse()));
                    }
                } else if (section.isList(key)) {
                    Optional<FlowType> flowType = FlowType.fromString(key.substring(0, 1));
                    List<ActionAPIType> expressions = new ArrayList<>();
                    for (String flowLine : section.getStringList(key)) {
                        if (parser.accept(flowLine)) {
                            expressions.add(parser.parse());
                        }
                    }
                    if (expressions.isEmpty()) {
                        throw new FlowException("Invalid list of flow actions/requirements in group alias: " + key);
                    }
                    aliasMap.put(key, new FlowAlias(flowType.orElse(expressions.get(0).getFlowType()), key, expressions));
                }
            } catch (FlowException e) {
                e.printStackTrace();
            }
        }

        RaidCraft.LOGGER.info("Loaded " + aliasMap.size() + " alias groups for " + ConfigUtil.getFileName(getConfig()) + " -> " + section.getName());
    }

    private Optional<ConfigurationSection> getVariableGroupSection() {
        if (getConfig() == null) return Optional.empty();
        Set<String> keys = getConfig().getKeys(false);

        if (keys == null || keys.isEmpty()) return Optional.empty();

        for (String key : keys) {
            if (getConfig().isConfigurationSection(key)) {
                return Optional.ofNullable(getConfig().getConfigurationSection(key));
            }
        }

        return Optional.empty();
    }

    private Optional<Pair<String, List<String>>> getFlowStatements() {
        if (getConfig() == null) return Optional.empty();
        Set<String> keys = getConfig().getKeys(false);

        if (keys == null || keys.isEmpty()) return Optional.empty();

        for (String key : keys) {
            if (getConfig().isList(key)) {
                return Optional.of(new Pair<>(key, getConfig().getStringList(key)));
            }
        }

        return Optional.empty();
    }

    public List<Action<?>> parseActions() {
        return getFlowStatements()
                .map(pair -> new Pair<>(pair.getKey(), parse(pair.getValue())))
                .map(pair -> parseActions(pair.getKey(), pair.getValue()))
                .orElse(new ArrayList<>());
    }

    public List<Action<?>> parseActions(String key, List<FlowExpression> expressions) {
        List<Action<?>> actions = new ArrayList<>();

        long delay = 0;
        // we are gonna add all requirements to this list until an action is added
        boolean resetRequirements = false;
        List<Requirement<?>> applicableRequirements = new ArrayList<>();

        for (FlowExpression flowExpression : expressions) {
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

                        createGroupAction(expression)
                                .or(() -> ActionAPI.createAction(expression.getTypeId(), configuration))
                                .map(action -> {
                                    if (!applicableRequirements.isEmpty()) {
                                        applicableRequirements.forEach(action::addRequirement);
                                    }
                                    return action;
                                })
                                .ifPresentOrElse(actions::add, () -> ActionAPI.UNKNOWN_ACTIONS.add(expression.getTypeId()));

                        if (!applicableRequirements.isEmpty()) {
                            resetRequirements = true;
                        }
                        break;
                    case REQUIREMENT:
                        // clear all requirements after an action was added
                        if (resetRequirements) {
                            applicableRequirements.clear();
                            resetRequirements = false;
                        }

                        String id = ConfigUtil.getFileName(getConfig()) + "." + key + "." + expression.getTypeId();

                        createGroupRequirement(id, expression)
                                .or(() -> ActionAPI.createRequirement(id, expression.getTypeId(), expression.getConfiguration()))
                                .ifPresentOrElse(applicableRequirements::add,
                                        () -> ActionAPI.UNKNOWN_REQUIREMENTS.add(expression.getTypeId()));
                        break;
                }
            }
        }

        return actions;
    }

    @SuppressWarnings("unchecked")
    private Optional<Action<?>> createGroupAction(ActionAPIType expression) {

        if (aliasMap.containsKey(expression.getTypeId())) {
            FlowAlias alias = aliasMap.get(expression.getTypeId());
            // only add aliases that match the flow type
            if (alias.getFlowType() == expression.getFlowType()) {
                // create a group action from the recursive action list of our alias group
                GroupAction groupAction = ActionAPI.createAction(GroupAction.class, expression.getConfiguration());
                groupAction.getActions().addAll(parseActions(alias.getAlias(), alias.getExpressions()));
                return Optional.of(groupAction);
            }
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private Optional<Requirement<?>> createGroupRequirement(String id, ActionAPIType expression) {

        if (aliasMap.containsKey(expression.getTypeId())) {
            FlowAlias alias = aliasMap.get(expression.getTypeId());
            // only add aliases that match the flow type
            if (alias.getFlowType() == expression.getFlowType()) {
                // recurse and add all requirements from our alias
                GroupRequirement requirement = ActionAPI.createRequirement(id, GroupRequirement.class, expression.getConfiguration());
                requirement.getRequirements().addAll(parseRequirements(id, alias.getExpressions()));
                return Optional.of(requirement);
            }
        }
        return Optional.empty();
    }

    public List<TriggerFactory> parseTrigger() {

        ArrayList<TriggerFactory> factories = new ArrayList<>();
        Optional<Pair<String, List<String>>> flowStatements = getFlowStatements();
        if (!flowStatements.isPresent()) return new ArrayList<>();

        long delay = 0;
        List<FlowExpression> flowExpressions = parse(flowStatements.get().getValue());
        // we are gonna add all requirements to this list until an action is added
        List<ActionAPIType> applicableRequirements = new ArrayList<>();
        TriggerFactory activeTrigger = null;

        int i = 0;
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
                        TriggerFactory trigger = ActionAPI
                                .createTrigger(((ActionAPIType) flowExpression).getTypeId(), configuration);
                        activeTrigger = trigger;
                        factories.add(trigger);
                        // reset the delay when a new trigger starts
                        delay = 0;
                        break;
                    case ACTION:
                        if (activeTrigger != null) {
                            String actionId = "actions.flow-" + i++;
                            configuration.set("delay", delay);
                            if (!applicableRequirements.isEmpty()) {
                                for (ActionAPIType requirement : applicableRequirements) {
                                    configuration.set(actionId + ".requirements.flow-" + i++,
                                            requirement.getConfiguration());
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

        return factories;
    }

    public List<Requirement<?>> parseRequirements(String id) {
        return getFlowStatements()
                .map(stringListPair -> parseRequirements(id + "." + stringListPair.getKey(), parse(stringListPair.getValue())))
                .orElse(new ArrayList<>());
    }

    public List<Requirement<?>> parseRequirements(String id, List<FlowExpression> expressions) {
        List<Requirement<?>> requirements = new ArrayList<>();

        long delay = 0;

        for (FlowExpression flowExpression : expressions) {
            if (flowExpression instanceof FlowDelay) {
                delay += ((FlowDelay) flowExpression).getDelay();
                continue;
            }
            if (flowExpression instanceof ActionAPIType) {
                ActionAPIType expression = (ActionAPIType) flowExpression;
                FlowConfiguration configuration = expression.getConfiguration();
                switch (expression.getFlowType()) {
                    case ACTION:
                        if (requirements.isEmpty())
                            continue;
                        configuration.set("delay", delay);

                        createGroupAction(expression)
                                .or(() -> ActionAPI.createAction(expression.getTypeId(), configuration))
                                .ifPresentOrElse(action -> requirements.get(requirements.size() - 1).addAction(action),
                                        () -> ActionAPI.UNKNOWN_ACTIONS.add(expression.getTypeId()));
                        break;
                    case REQUIREMENT:
                        createGroupRequirement(id, expression)
                                .or(() -> ActionAPI.createRequirement(id, expression.getTypeId(), expression.getConfiguration()))
                                .ifPresentOrElse(requirements::add,
                                        () -> ActionAPI.UNKNOWN_REQUIREMENTS.add(expression.getTypeId()));
                        break;
                }
            }
        }

        return requirements;
    }

    public List<Answer> parseAnswers(StageTemplate template) {

        ArrayList<Answer> answers = new ArrayList<>();
        Optional<Pair<String, List<String>>> flowStatements = getFlowStatements();
        if (!flowStatements.isPresent()) return new ArrayList<>();
        String key = flowStatements.get().getKey();

        try {
            long delay = 0;
            List<FlowExpression> flowExpressions = parse(flowStatements.get().getValue());
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
                            if (activeAnswer == null)
                                continue;
                            configuration.set("delay", delay);
                            Optional<Action<Conversation>> convAction = ActionAPI
                                    .createAction(expression.getTypeId(), configuration, Conversation.class);
                            if (convAction.isPresent()) {
                                Action<Conversation> action = convAction.get();
                                activeAnswer.addConversationAction(action);
                                if (!conversationRequirements.isEmpty()) {
                                    conversationRequirements.forEach(action::addRequirement);
                                }
                            } else {
                                Optional<Action<Player>> playerAction = ActionAPI
                                        .createAction(expression.getTypeId(), configuration, Player.class);
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
                                    ConfigUtil.getFileName(config) + key, expression.getTypeId(),
                                    expression.getConfiguration(), Player.class);
                            if (!playerRequirement.isPresent()) {
                                Optional<Requirement<Conversation>> conversationRequirement = ActionAPI
                                        .createRequirement(ConfigUtil.getFileName(config) + key,
                                                expression.getTypeId(), expression.getConfiguration(),
                                                Conversation.class);
                                if (conversationRequirement.isPresent()) {
                                    throw new FlowException(
                                            "Could not find valid requirement type for " + expression.getTypeId());
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
            RaidCraft.LOGGER.warning("Error when parsing " + key + " inside " + ConfigUtil.getFileName(config)
                    + ": " + e.getMessage());
        }

        return answers;
    }

    private List<FlowExpression> parse(List<String> lines) {

        List<FlowExpression> expressions = new ArrayList<>();

        for (String line : lines) {
            try {
                for (FlowParser parser : parsers) {
                    if (parser.accept(line)) {
                        expressions.add(parser.parse());
                    }
                }
            } catch (FlowException e) {
                RaidCraft.LOGGER.warning(
                        "Error when parsing line " + line + " inside " + ConfigUtil.getFileName(config) + ": " + e.getMessage());
            }
        }
        return expressions;
    }
}
