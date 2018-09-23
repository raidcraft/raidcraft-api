package de.raidcraft.api.action.flow;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.ActionConfigWrapper;
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

    private final FlowParser[] parsers = {new ActionApiFlowParser(this), new AnswerParser()};

    private final ConfigurationSection config;

    private final Map<FlowType, Map<String, FlowAlias>> aliasMap = Map.ofEntries(
            Map.entry(FlowType.ACTION, new HashMap<>()),
            Map.entry(FlowType.REQUIREMENT, new HashMap<>()),
            Map.entry(FlowType.ANSWER, new HashMap<>()),
            Map.entry(FlowType.TRIGGER, new HashMap<>()),
            Map.entry(FlowType.EXPRESSION, new HashMap<>())
    );

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
                        FlowAlias alias = new FlowAlias(key, parser.parse());
                        aliasMap.get(alias.getFlowType()).put(key, alias);
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
                    FlowAlias alias = new FlowAlias(flowType.orElse(expressions.get(0).getFlowType()), key, expressions);
                    aliasMap.get(alias.getFlowType()).put(key, alias);
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

                        createAction(expression)
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

                        createRequirement(id, expression)
                                .ifPresentOrElse(applicableRequirements::add,
                                        () -> ActionAPI.UNKNOWN_REQUIREMENTS.add(expression.getTypeId()));
                        break;
                }
            }
        }

        return actions;
    }

    @SuppressWarnings("unchecked")
    private Optional<ActionConfigWrapper<?>> createGroupAction(ActionAPIType expression) {

        if (!hasAlias(expression.getFlowType(), expression.getTypeId())) {
            return Optional.empty();
        }

        FlowAlias flowAlias = getAlias(expression.getFlowType(), expression.getTypeId());
        List<Action<?>> actions = parseActions(flowAlias.getAlias(), flowAlias.getExpressions());

        // create a group action from the recursive action list of our alias group
        ActionConfigWrapper<?> configWrapper = ActionAPI.createAction(GroupAction.class, expression.getConfiguration());
        configWrapper.getActions().addAll(actions);

        return Optional.of(configWrapper);
    }

    @SuppressWarnings("unchecked")
    private Optional<Requirement<?>> createGroupRequirement(String id, ActionAPIType expression) {

        if (!hasAlias(expression.getFlowType(), expression.getTypeId())) {
            return Optional.empty();
        }

        FlowAlias flowAlias = getAlias(expression.getFlowType(), expression.getTypeId());

        // recurse and add all requirements from our alias
        GroupRequirement requirement = ActionAPI.createRequirement(id, GroupRequirement.class, expression.getConfiguration());
        requirement.getRequirements().addAll(parseRequirements(id, flowAlias.getExpressions()));
        return Optional.of(requirement);
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
                        TriggerFactory trigger = ActionAPI.createTrigger(((ActionAPIType) flowExpression).getTypeId(), configuration);

                        trigger.getRequirements().addAll(parseRequirements("", new ArrayList<>(applicableRequirements)));
                        applicableRequirements.clear();

                        activeTrigger = trigger;
                        factories.add(trigger);
                        // reset the delay when a new trigger starts
                        delay = 0;
                        break;
                    case ACTION:
                        if (activeTrigger != null) {
                            String actionId = "actions.flow-" + i++;
                            configuration.set("delay", delay);
                            ArrayList<FlowExpression> expressions = new ArrayList<>();
                            expressions.add(expression);
                            List<Action<?>> actions = parseActions(actionId, expressions);
                            for (Requirement<?> requirement : parseRequirements("", new ArrayList<>(applicableRequirements))) {
                                actions.forEach(action -> action.addRequirement(requirement));
                            }
                            applicableRequirements.clear();
                            activeTrigger.getActions().addAll(actions);
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

                        createAction(expression)
                                .ifPresentOrElse(action -> requirements.get(requirements.size() - 1).addAction(action),
                                        () -> ActionAPI.UNKNOWN_ACTIONS.add(expression.getTypeId()));
                        break;
                    case REQUIREMENT:
                        createRequirement(id, expression)
                                .ifPresentOrElse(requirements::add,
                                        () -> ActionAPI.UNKNOWN_REQUIREMENTS.add(expression.getTypeId()));
                        break;
                }
            }
        }

        return requirements;
    }

    private Optional<ActionConfigWrapper<?>> createAction(ActionAPIType expression) {
        return createGroupAction(expression)
                .or(() -> ActionAPI.createAction(expression.getTypeId(), expression.getConfiguration()));
    }

    private Optional<Requirement<?>> createRequirement(String id, ActionAPIType expression) {
        return createGroupRequirement(id, expression)
                .or(() -> ActionAPI.createRequirement(id, expression.getTypeId(), expression.getConfiguration()));
    }

    public List<Answer> parseAnswers(StageTemplate template) {

        ArrayList<Answer> answers = new ArrayList<>();
        Optional<Pair<String, List<String>>> flowStatements = getFlowStatements();
        if (!flowStatements.isPresent()) return new ArrayList<>();
        String key = flowStatements.get().getKey();

        try {
            long delay = 0;
            List<FlowExpression> flowExpressions = parse(flowStatements.get().getValue());
            List<Requirement<?>> requirements = new ArrayList<>();
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

                    for (Requirement<?> requirement : requirements) {
                        if (ActionAPI.matchesType(requirement, Player.class)) {
                            activeAnswer.addPlayerRequirement((Requirement<Player>) requirement);
                        } else if (ActionAPI.matchesType(requirement, Conversation.class)) {
                            activeAnswer.addConversationRequirement((Requirement<Conversation>) requirement);
                        } else {
                            activeAnswer.addRequirement(requirement);
                        }
                    }
                    requirements.clear();
                } else if (flowExpression instanceof ActionAPIType) {
                    ActionAPIType expression = (ActionAPIType) flowExpression;
                    FlowConfiguration configuration = expression.getConfiguration();
                    switch (expression.getFlowType()) {
                        case ACTION:
                            if (activeAnswer == null)
                                continue;
                            configuration.set("delay", delay);
                            Action<?> action = createAction(expression)
                                    .orElseThrow(() -> new FlowException("Could not find valid action type for " + expression.getTypeId()));

                            if (ActionAPI.matchesType(action, Player.class)) {
                                activeAnswer.addPlayerAction((Action<Player>) action);
                            } else if (ActionAPI.matchesType(action, Conversation.class)) {
                                activeAnswer.addConversationAction((Action<Conversation>) action);
                            } else {
                                activeAnswer.addActions(action);
                            }
                            requirements.forEach(action::addRequirement);
                            requirements.clear();
                            break;
                        case REQUIREMENT:
                            requirements.add(createRequirement(ConfigUtil.getFileName(config) + key, expression)
                                    .orElseThrow(() -> new FlowException("Could not find valid requirement type for " + expression.getTypeId())));
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

    public boolean hasAlias(FlowType type, String alias) {
        return aliasMap.get(type).containsKey(alias);
    }

    public FlowAlias getAlias(FlowType type, String alias) {
        return aliasMap.get(type).get(alias);
    }
}
