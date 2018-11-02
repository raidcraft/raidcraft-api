package de.raidcraft.api.action.flow;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.ActionConfigWrapper;
import de.raidcraft.api.action.RequirementConfigWrapper;
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
import de.raidcraft.util.Tuple;
import io.ebeaninternal.server.expression.Op;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Parses a given config section for all flow statements and returns a list of actions, requirements or triggers.
 * Manages local variable groups for the parsing flow block.
 */
@Data
public class FlowConfigParser {

    private static final String VARIABLE_GROUPS_SECTION = "groups";
    private static final String FLOW_SECTION = "flow";

    private final FlowParser[] parsers = {new ActionApiFlowParser(this), new AnswerParser()};

    private final ConfigurationSection config;
    private final String baseId;
    private GlobalFlowParameters globalParameters;

    private final Map<FlowType, Map<String, FlowAlias>> aliasMap = new HashMap<>();

    public FlowConfigParser(ConfigurationSection config) {
        this.config = config;
        this.baseId = ConfigUtil.getFileName(getConfig())
                .replace("/", ".")
                .replace("\\", ".")
                .replace(".yml", "")
                .replace(" ", "-")
                .toLowerCase() + config.getCurrentPath();
        this.aliasMap.put(FlowType.ACTION, new HashMap<>());
        this.aliasMap.put(FlowType.REQUIREMENT, new HashMap<>());
        this.aliasMap.put(FlowType.ANSWER, new HashMap<>());
        this.aliasMap.put(FlowType.TRIGGER, new HashMap<>());
        this.aliasMap.put(FlowType.EXPRESSION, new HashMap<>());
        getVariableGroupSection().ifPresent(this::loadVariableGroups);
        if (config.isConfigurationSection("options")) {
            this.globalParameters = new GlobalFlowParameters(getConfig().getConfigurationSection("options"));
        }
    }

    public Optional<GlobalFlowParameters> getGlobalParameters() {
        return Optional.ofNullable(this.globalParameters);
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
                RaidCraft.LOGGER.warning(e.getMessage());
            }
        }

        int groupCount = aliasMap.entrySet().stream().mapToInt(flowTypeMapEntry -> flowTypeMapEntry.getValue().size()).sum();
        if (RaidCraft.getComponent(RaidCraftPlugin.class).getConfig().debugFlowParser) {
            RaidCraft.LOGGER.info("Loaded " + groupCount + " alias groups for " + ConfigUtil.getFileName(getConfig()) + " -> " + section.getCurrentPath());
        }
    }

    private Optional<ConfigurationSection> getVariableGroupSection() {
        if (getConfig() == null) return Optional.empty();
        Set<String> keys = getConfig().getKeys(false);

        if (keys == null || keys.isEmpty() || !getConfig().isConfigurationSection(VARIABLE_GROUPS_SECTION)) return Optional.empty();

        return Optional.of(getConfig().getConfigurationSection(VARIABLE_GROUPS_SECTION));
    }

    private List<String> getFlowStatements() {

        if (getConfig() == null || !getConfig().isList(FLOW_SECTION)) return new ArrayList<>();

        return getConfig().getStringList(FLOW_SECTION);
    }

    public List<Action<?>> parseActions() {
        return parseActions(FLOW_SECTION, parse(getFlowStatements()));
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
                                .ifPresent(actions::add);

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

                        String id = getBaseId() + "." + key + "." + expression.getTypeId();

                        createRequirement(id, expression)
                                .ifPresent(applicableRequirements::add);
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
    private Optional<RequirementConfigWrapper<?>> createGroupRequirement(String id, ActionAPIType expression) {

        if (!hasAlias(expression.getFlowType(), expression.getTypeId())) {
            return Optional.empty();
        }

        FlowAlias flowAlias = getAlias(expression.getFlowType(), expression.getTypeId());

        // recurse and add all requirements from our alias
        RequirementConfigWrapper<?> requirement = ActionAPI.createRequirement(id, GroupRequirement.class, expression.getConfiguration());
        requirement.getRequirements().addAll(parseRequirements(id, flowAlias.getExpressions()));
        return Optional.of(requirement);
    }

    public List<TriggerFactory> parseTrigger() {

        ArrayList<TriggerFactory> factories = new ArrayList<>();
        List<String> flowStatements = getFlowStatements();
        if (flowStatements.isEmpty()) return new ArrayList<>();

        long delay = 0;
        List<FlowExpression> flowExpressions = parse(flowStatements);
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
                        // invalidate the trigger after the delay if set
                        // only for ordered triggers
                        configuration.set("valid", delay);
                        TriggerFactory trigger = ActionAPI.createTrigger(((ActionAPIType) flowExpression).getTypeId(), configuration);
                        String triggerId = getBaseId() + "." + "trigger.flow-" + i++;

                        trigger.getRequirements().addAll(parseRequirements(triggerId, new ArrayList<>(applicableRequirements)));
                        applicableRequirements.clear();

                        activeTrigger = trigger;
                        factories.add(trigger);
                        // reset the delay when a new trigger starts
                        delay = 0;
                        break;
                    case ACTION:
                        if (activeTrigger != null) {
                            String actionId = getBaseId() + "." + "actions.flow-" + i++;
                            configuration.set("delay", delay);
                            ArrayList<FlowExpression> expressions = new ArrayList<>();
                            expressions.add(expression);
                            List<Action<?>> actions = parseActions(actionId, expressions);
                            for (Requirement<?> requirement : parseRequirements(actionId, new ArrayList<>(applicableRequirements))) {
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

    public List<RequirementConfigWrapper<?>> parseRequirements(String id) {
        return parseRequirements(id + "." + FLOW_SECTION, parse(getFlowStatements()));
    }

    public List<RequirementConfigWrapper<?>> parseRequirements(String id, List<FlowExpression> expressions) {
        List<RequirementConfigWrapper<?>> requirements = new ArrayList<>();

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
                                .ifPresent(action -> requirements.get(requirements.size() - 1).addAction(action));
                        break;
                    case REQUIREMENT:
                        createRequirement(id, expression)
                                .ifPresent(requirements::add);
                        break;
                }
            }
        }

        return requirements;
    }

    private Optional<ActionConfigWrapper<?>> createAction(ActionAPIType expression) {
        return Optional.ofNullable(createGroupAction(expression)
                .orElseGet(() -> ActionAPI.createAction(expression.getTypeId(), expression.getConfiguration()).orElse(null)));
    }

    private Optional<RequirementConfigWrapper<?>> createRequirement(String id, ActionAPIType expression) {
        return Optional.ofNullable(createGroupRequirement(id, expression)
                .orElseGet(() -> ActionAPI.createRequirement(id, expression.getTypeId(), expression.getConfiguration()).orElse(null)));
    }

    public List<Answer> parseAnswers(StageTemplate template) {

        ArrayList<Answer> answers = new ArrayList<>();
        List<String> flowStatements = getFlowStatements();
        if (flowStatements.isEmpty()) return new ArrayList<>();
        String key = FLOW_SECTION;

        try {
            long delay = 0;
            List<FlowExpression> flowExpressions = parse(flowStatements);
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
                            requirements.add(createRequirement(getBaseId() + ".requirement." + key, expression)
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
                        FlowExpression expression = parser.parse();
                        if (expression instanceof ActionAPIType) {
                            getGlobalParameters().ifPresent(params -> {
                                ((ActionAPIType) expression).getConfiguration().set("worlds", params.getWorlds());
                                ((ActionAPIType) expression).getConfiguration().set("regions", params.getRegions());
                            });
                        }
                        expressions.add(expression);
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
