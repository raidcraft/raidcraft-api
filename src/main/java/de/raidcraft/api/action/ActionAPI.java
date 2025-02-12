package de.raidcraft.api.action;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.flow.Flow;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.trigger.Trigger;
import de.raidcraft.api.config.builder.ConfigBuilder;
import de.raidcraft.api.config.builder.ConfigGenerator;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mdoering
 */
public final class ActionAPI {

    public static final Set<String> UNKNOWN_ACTIONS = new HashSet<>();
    public static final Set<String> UNKNOWN_REQUIREMENTS = new HashSet<>();
    public static final Set<String> UNKNOWN_TRIGGER = new HashSet<>();

    private static final Map<Class<?>, ActionFactory<?>> actionFactories = new HashMap<>();
    private static final Map<Class<?>, RequirementFactory<?>> requirementFactories = new HashMap<>();
    private static final TriggerManager triggerManager = new TriggerManager();

    public static String getIdentifier(Object object) {

        if (object instanceof Action) {
            Optional<Optional<String>> optional = actionFactories.values().stream()
                    .map(actionFactory -> actionFactory.getActionIdentifier((Action) object))
                    .filter(Optional::isPresent)
                    .findFirst();
            if (optional.isPresent()) {
                return optional.get().get();
            }
        } else if (object instanceof Requirement) {
            Optional<Optional<String>> optional = requirementFactories.values().stream()
                    .map(requirementFactory -> requirementFactory.getRequirementIdentifier((Requirement) object))
                    .filter(Optional::isPresent)
                    .findFirst();
            if (optional.isPresent()) {
                return optional.get().get();
            }
        } else if (object instanceof Trigger) {
            return ((Trigger) object).getIdentifier();
        }
        return "undefined";
    }

    public static Optional<ConfigGenerator.Information> getActionInformation(String identifier) {

        Optional<ActionFactory<?>> factory = actionFactories.values().stream()
                .filter(actionFactory -> actionFactory.contains(identifier))
                .findAny();
        if (factory.isPresent()) {
            return factory.get().getInformation(identifier);
        }
        return Optional.empty();
    }

    public static Optional<ConfigGenerator.Information> getRequirementInformation(String identifier) {

        Optional<RequirementFactory<?>> factory = requirementFactories.values().stream()
                .filter(requirementFactory -> requirementFactory.contains(identifier))
                .findAny();
        if (factory.isPresent()) {
            return factory.get().getInformation(identifier);
        }
        return Optional.empty();
    }

    public static Optional<ConfigGenerator.Information> getTriggerInformation(String identifier) {

        return triggerManager.getInformation(identifier);
    }

    public static Map<String, Action<?>> getActions() {

        Map<String, Action<?>> actions = new HashMap<>();
        actionFactories.values().forEach(actionFactory -> actions.putAll(actionFactory.getActions()));
        return actions;
    }

    public static Map<String, Requirement<?>> getRequirements() {

        Map<String, Requirement<?>> actions = new HashMap<>();
        requirementFactories.values().forEach(requirementFactory -> actions.putAll(requirementFactory.getRequirements()));
        return actions;
    }

    public static Map<String, Trigger> getTrigger() {

        return new HashMap<>(triggerManager.getTrigger());
    }

    public static boolean isAction(String identifier) {

        for (ActionFactory<?> factory : actionFactories.values()) {
            if (factory.contains(identifier)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRequirement(String identifier) {

        for (RequirementFactory<?> factory : requirementFactories.values()) {
            if (factory.contains(identifier)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTrigger(String identifier) {

        return triggerManager.contains(identifier);
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<RequirementConfigWrapper<T>> createRequirement(String id, String requirement, ConfigurationSection config, Class<T> type) {

        RequirementFactory<?> requirementFactory = requirementFactories.get(type);
        if (requirementFactory == null) return Optional.empty();
        return ((RequirementFactory<T>) requirementFactory).create(id, requirement, config);
    }

    public static Optional<RequirementConfigWrapper<?>> createRequirement(String id, String type, ConfigurationSection config) {

        Optional<RequirementFactory<?>> factory = requirementFactories.values().stream()
                .filter(requirementFactory -> requirementFactory.contains(type))
                .findAny();
        if (factory.isPresent()) {
            Optional<? extends RequirementConfigWrapper<?>> requirement = factory.get().create(id, type, config);
            if (requirement.isPresent()) {
                return Optional.of(requirement.get());
            }
        }
        return Optional.empty();
    }

    public static <T extends Requirement<?>> RequirementConfigWrapper<?> createRequirement(String id, Class<T> requirementClass, ConfigurationSection config) {
        ConfigGenerator.Information information = null;
        for (Method method : requirementClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ConfigGenerator.Information.class)) {
                information = method.getAnnotation(ConfigGenerator.Information.class);
            }
        }
        if (information == null) {
            throw new UnsupportedOperationException(requirementClass.getCanonicalName() + " has not @Information annotation defined!");
        }
        for (RequirementFactory<?> factory : requirementFactories.values()) {
            if (factory.contains(requirementClass)) {
                Optional<? extends RequirementConfigWrapper<?>> requirement = factory.create(id, requirementClass, config);
                if (requirement.isPresent()) {
                    return requirement.get();
                }
            }
        }
        throw new UnsupportedOperationException("Could not find matching action for " + requirementClass.getCanonicalName() + " -> " + information.value());
    }

    public static <T> List<Requirement<T>> createRequirements(String id, ConfigurationSection requirements, Class<T> type) {

        return createRequirements(id, requirements).stream()
                .filter(requirement -> matchesType(requirement, type))
                .map(requirement -> (RequirementConfigWrapper<T>) requirement)
                .collect(Collectors.toList());
    }

    public static List<Requirement<?>> createRequirements(String id, ConfigurationSection requirements) {

        return new ArrayList<>(Flow.parseRequirements(id, requirements));
    }

    public static <T> Optional<ActionConfigWrapper<T>> createAction(String identifier, ConfigurationSection config, Class<T> type) {

        ActionFactory<?> actionFactory = actionFactories.get(type);
        if (actionFactory == null) return Optional.empty();
        return ((ActionFactory<T>) actionFactory).create(identifier, config);
    }

    public static Optional<ActionConfigWrapper<?>> createAction(String identifier, ConfigurationSection config) {

        Optional<ActionFactory<?>> factory = actionFactories.values().stream().filter(actionFactory -> actionFactory.contains(identifier)).findAny();
        if (factory.isPresent()) {
            Optional<? extends ActionConfigWrapper<?>> wrapper = factory.get().create(identifier, config);
            if (wrapper.isPresent()) {
                return Optional.of(wrapper.get());
            }
        }
        return Optional.empty();
    }

    public static <T extends Action<?>> ActionConfigWrapper<?> createAction(Class<T> actionClass) {

        return createAction(actionClass, new MemoryConfiguration());
    }

    public static <T extends Action<?>> ActionConfigWrapper<?> createAction(Class<T> actionClass, ConfigurationSection config) {
        ConfigGenerator.Information information = null;
        for (Method method : actionClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ConfigGenerator.Information.class)) {
                information = method.getAnnotation(ConfigGenerator.Information.class);
            }
        }
        if (information == null) {
            throw new UnsupportedOperationException(actionClass.getCanonicalName() + " has not @Information annotation defined!");
        }
        for (ActionFactory<?> factory : actionFactories.values()) {
            if (factory.contains(actionClass)) {
                Optional<? extends ActionConfigWrapper<?>> configWrapper = factory.create(actionClass, config);
                if (configWrapper.isPresent()) {
                    return configWrapper.get();
                }
            }
        }
        throw new UnsupportedOperationException("Could not find matching action for " + actionClass.getCanonicalName() + " -> " + information.value());
    }

    @SuppressWarnings("unchecked")
    public static <T> List<Action<T>> createActions(ConfigurationSection actions, Class<T> type) {

        return createActions(actions).stream()
                .filter(action -> matchesType(action, type))
                .map(action -> (Action<T>) action)
                .collect(Collectors.toList());
    }

    /**
     * Creates a list of actions using flow syntax.
     * Will search for a string list to parse as flow section in all sub sections.
     * Valid config sections are as follows:
     * actions:
     *   group:
     *     group1: !action
     *     !group2:
     *       - !foo
     *       - !bar
     *   flow:
     *     - !foo
     *
     * @param actions config section to create action list from
     * @return action list
     */
    public static List<Action<?>> createActions(ConfigurationSection actions) {

        return Flow.parseActions(actions);
    }

    public static TriggerFactory createTrigger(String identifier, ConfigurationSection config) {

        return triggerManager.createTrigger(identifier, config);
    }

    public static List<TriggerFactory> createTrigger(ConfigurationSection trigger) {

        return triggerManager.createTriggerFactories(trigger);
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<RequirementFactory<T>> getRequirementFactory(Class<T> type) {

        RequirementFactory<?> factory = requirementFactories.get(type);
        if (factory == null) return Optional.empty();
        return Optional.of((RequirementFactory<T>) factory);
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<ActionFactory<T>> getActionFactory(Class<T> type) {

        ActionFactory<?> factory = actionFactories.get(type);
        if (factory == null) return Optional.empty();
        return Optional.of((ActionFactory<T>) factory);
    }

    public static boolean matchesType(Action<?> action, Class<?> type) {

        if (action instanceof ActionConfigWrapper) {
            return ((ActionConfigWrapper) action).getType().isAssignableFrom(type);
        }
        ActionFactory<?> actionFactory = actionFactories.get(type);
        return actionFactory != null && actionFactory.contains(action);
    }

    public static <T> List<Action<T>> filterActionTypes(Collection<Action<?>> actions, Class<T> type) {
        return actions.stream()
                .filter(action -> matchesType(action, type))
                .map(action -> (Action<T>) action)
                .collect(Collectors.toList());
    }

    public static <T> List<Requirement<T>> filterRequirementTypes(Collection<Requirement<?>> requirements, Class<T> type) {
        return requirements.stream()
                .filter(requirement -> matchesType(requirement, type))
                .map(requirement -> (Requirement<T>) requirement)
                .collect(Collectors.toList());
    }

    public static boolean matchesType(Requirement<?> requirement, Class<?> type) {

        if (requirement instanceof RequirementConfigWrapper) {
            return ((RequirementConfigWrapper) requirement).getType().isAssignableFrom(type);
        }
        RequirementFactory<?> requirementFactory = requirementFactories.get(type);
        return requirementFactory != null && requirementFactory.contains(requirement);
    }

    public static ActionAPI register(BasePlugin plugin) {

        return new ActionAPI(plugin);
    }

    private final BasePlugin plugin;
    private boolean global = false;

    private ActionAPI(BasePlugin plugin) {

        this.plugin = plugin;
    }


    @SuppressWarnings("unchecked")
    private <T> ActionAPI action(@NonNull String identifier, @NonNull Action<T> action, Class<T> type, String... aliases) {

        ActionFactory<T> factory;
        if (!actionFactories.containsKey(type)) {
            factory = new ActionFactory<>(type);
            actionFactories.put(type, factory);
        } else {
            factory = (ActionFactory<T>) actionFactories.get(type);
        }
        if (global) {
            factory.registerGlobalAction(identifier, action);
            for (String alias : aliases) {
                factory.addAlias(identifier, alias);
            }
        } else {
            factory.registerAction(plugin, identifier, action);
            for (String alias : aliases) {
                factory.addAlias(plugin, identifier, alias);
            }
        }
        return this;
    }


    /**
     * @deprecated use {@link #action(Action, Class, String[])} or {@link #action(Action, String[])}
     */
    @Deprecated
    public ActionAPI action(@NonNull String identifier, @NonNull Action<Player> action, String... aliases) {

        return action(identifier, action, Player.class, aliases);
    }

    @SuppressWarnings("unchecked")
    private <T> ActionAPI requirement(@NonNull String identifier, @NonNull Requirement<T> requirement, Class<T> type, String... aliases) {

        RequirementFactory<T> factory;
        if (!requirementFactories.containsKey(type)) {
            factory = new RequirementFactory<>(type);
            requirementFactories.put(type, factory);
        } else {
            factory = (RequirementFactory<T>) requirementFactories.get(type);
        }
        if (global) {
            factory.registerGlobalRequirement(identifier, requirement);
            for (String alias : aliases) {
                factory.addAlias(identifier, alias);
            }
        } else {
            factory.registerRequirement(plugin, identifier, requirement);
            for (String alias : aliases) {
                factory.addAlias(plugin, identifier, alias);
            }
        }
        return this;
    }

    /**
     * @deprecated use {@link #requirement(Requirement, Class, String[])} or {@link #requirement(Requirement, String[])}
     */
    @Deprecated
    public ActionAPI requirement(@NonNull String identifier, @NonNull Requirement<Player> requirement, String... aliases) {

        return requirement(identifier, requirement, Player.class, aliases);
    }

    public ActionAPI trigger(@NonNull Trigger trigger) {

        if (global) {
            triggerManager.registerGlobalTrigger(trigger);
        } else {
            triggerManager.registerTrigger(plugin, trigger);
        }
        return this;
    }

    public ActionAPI action(@NonNull Action<Player> action, String... aliases) {

        return action(action, Player.class, aliases);
    }

    public <T> ActionAPI action(@NonNull Action<T> action, Class<T> type, String... aliases) {

        Optional<ConfigGenerator.Information> information = ConfigBuilder.getInformation(action);
        if (!information.isPresent()) {
            plugin.getLogger().warning("Tried to register ACTION without identifier and @Information Tag: " + action.getClass().getCanonicalName());
            return this;
        }
        String identifier = information.get().value();
        aliases = (String[]) ArrayUtils.addAll(aliases, information.get().aliases());
        return action(identifier, action, type, aliases);
    }

    public ActionAPI requirement(@NonNull Requirement<Player> requirement, String... aliases) {

        return requirement(requirement, Player.class, aliases);
    }

    public <T> ActionAPI requirement(@NonNull Requirement<T> requirement, Class<T> type, String... aliases) {

        Optional<ConfigGenerator.Information> information = ConfigBuilder.getInformation(requirement);
        if (!information.isPresent()) {
            plugin.getLogger().warning("Tried to register REQUIREMENT without identifier and @Information Tag: " + requirement.getClass().getCanonicalName());
            return this;
        }
        String identifier = information.get().value();
        aliases = (String[]) ArrayUtils.addAll(aliases, information.get().aliases());
        return requirement(identifier, requirement, type, aliases);
    }

    public ActionAPI global() {

        global = true;
        return this;
    }

    public ActionAPI local() {

        global = false;
        return this;
    }

    public static class Helper {

        public static <T> Optional<Requirement<T>> createExecuteOnceRequirement(String id, Class<T> type) {

            MemoryConfiguration configuration = new MemoryConfiguration();
            configuration.set("persistent", true);
            Optional<RequirementFactory<T>> factory = ActionAPI.getRequirementFactory(type);
            if (factory.isPresent()) {
                return factory.get().create(
                        id + "." + GlobalRequirement.EXECUTE_ONCE_TRIGGER.getId(),
                        GlobalRequirement.EXECUTE_ONCE_TRIGGER.getId(),
                        configuration).map(wrapper -> wrapper);
            }
            return Optional.empty();
        }

        public static <T> Optional<Requirement<T>> createCooldownRequirement(String id, long cooldown, Class<T> type) {

            MemoryConfiguration configuration = new MemoryConfiguration();
            configuration.set("args.cooldown", cooldown);
            configuration.set("persistent", true);
            Optional<RequirementFactory<T>> factory = ActionAPI.getRequirementFactory(type);
            if (factory.isPresent()) {
                return factory.get().create(
                        id + "." + GlobalRequirement.COOLDOWN.getId(),
                        GlobalRequirement.COOLDOWN.getId(),
                        configuration).map(wrapper -> wrapper);
            }
            return Optional.empty();
        }

        public static <T> Optional<Requirement<T>> createCountRequirement(String id, int count, String countText, Class<T> type) {
            MemoryConfiguration config = new MemoryConfiguration();
            config.set("count", count);
            config.set("count-text", countText);
            config.set("persistent", true);
            Optional<RequirementFactory<T>> factory = ActionAPI.getRequirementFactory(type);
            if (factory.isPresent()) {
                return factory.get().create(id + "." + GlobalRequirement.DUMMY.getId(),
                        GlobalRequirement.DUMMY.getId(),
                        config).map(wrapper -> wrapper);
            }
            return Optional.empty();
        }
    }
}
