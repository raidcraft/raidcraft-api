package de.raidcraft.api.action;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.trigger.Trigger;
import de.raidcraft.api.config.builder.ConfigBuilder;
import de.raidcraft.api.config.builder.ConfigGenerator;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author mdoering
 */
public final class ActionAPI {

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

    @SuppressWarnings("unchecked")
    public static <T> List<Requirement<T>> createRequirements(String id, ConfigurationSection requirements, Class<T> type) {

        RequirementFactory<?> factory = requirementFactories.get(type);
        if (factory == null) return new ArrayList<>();
        return ((RequirementFactory<T>) factory).createRequirements(id, requirements);
    }

    public static List<Requirement<?>> createRequirements(String id, ConfigurationSection requirements) {

        List<Requirement<?>> list = new ArrayList<>();
        for (RequirementFactory<?> requirementFactory : requirementFactories.values()) {
            list.addAll(requirementFactory.createRequirements(id, requirements));
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public static  <T> Collection<Action<T>> createActions(ConfigurationSection actions, Class<T> type) {

        ActionFactory<?> factory = actionFactories.get(type);
        if (factory == null) return new ArrayList<>();
        return ((ActionFactory<T>) factory).createActions(actions);
    }

    public static List<Action<?>> createActions(ConfigurationSection actions) {

        List<Action<?>> list = new ArrayList<>();
        for (ActionFactory<?> factory : actionFactories.values()) {
            list.addAll(factory.createActions(actions));
        }
        return list;
    }

    public static Collection<TriggerFactory> createTrigger(ConfigurationSection trigger) {

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
            return ((ActionConfigWrapper) action).getType().equals(type);
        }
        ActionFactory<?> actionFactory = actionFactories.get(type);
        return actionFactory != null && actionFactory.contains(action);
    }

    public static boolean matchesType(Requirement<?> requirement, Class<?> type) {

        if (requirement instanceof RequirementConfigWrapper) {
            return ((RequirementConfigWrapper) requirement).getType().equals(type);
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
    public <T> ActionAPI action(@NonNull String identifier, @NonNull Action<T> action, Class<T> type) {

        ActionFactory<T> factory;
        if (!actionFactories.containsKey(type)) {
            factory = new ActionFactory<>(type);
            actionFactories.put(type, factory);
        } else {
            factory = (ActionFactory<T>) actionFactories.get(type);
        }
        if (global) {
            factory.registerGlobalAction(identifier, action);
        } else {
            factory.registerAction(plugin, identifier, action);
        }
        return this;
    }

    public ActionAPI action(@NonNull String identifier, @NonNull Action<Player> action) {

        return action(identifier, action, Player.class);
    }

    @SuppressWarnings("unchecked")
    public <T> ActionAPI requirement(@NonNull String identifier, @NonNull Requirement<T> requirement, Class<T> type) {

        RequirementFactory<T> factory;
        if (!requirementFactories.containsKey(type)) {
            factory = new RequirementFactory<>(type);
            requirementFactories.put(type, factory);
        } else {
            factory = (RequirementFactory<T>) requirementFactories.get(type);
        }
        if (global) {
            factory.registerGlobalRequirement(identifier, requirement);
        } else {
            factory.registerRequirement(plugin, identifier, requirement);
        }
        return this;
    }

    public ActionAPI requirement(@NonNull String identifier, @NonNull Requirement<Player> requirement) {

        return requirement(identifier, requirement, Player.class);
    }

    public ActionAPI trigger(@NonNull Trigger trigger) {

        if (global) {
            triggerManager.registerGlobalTrigger(trigger);
        } else {
            triggerManager.registerTrigger(plugin, trigger);
        }
        return this;
    }

    public ActionAPI action(@NonNull Action<Player> requirement) {

        return action(requirement, Player.class);
    }

    public <T> ActionAPI action(@NonNull Action<T> action, Class<T> type) {

        Optional<ConfigGenerator.Information> information = ConfigBuilder.getInformation(action);
        if (!information.isPresent()) {
            plugin.getLogger().warning("Tried to register ACTION without identifier and @Information Tag: " + action.getClass().getCanonicalName());
            return this;
        }
        String identifier = information.get().value();
        return action(identifier, action, type);
    }

    public ActionAPI requirement(@NonNull Requirement<Player> requirement) {

        return requirement(requirement, Player.class);
    }

    public <T> ActionAPI requirement(@NonNull Requirement<T> requirement, Class<T> type) {

        Optional<ConfigGenerator.Information> information = ConfigBuilder.getInformation(requirement);
        if (!information.isPresent()) {
            plugin.getLogger().warning("Tried to register REQUIREMENT without identifier and @Information Tag: " + requirement.getClass().getCanonicalName());
            return this;
        }
        String identifier = information.get().value();
        return requirement(identifier, requirement, type);
    }

    public ActionAPI global() {

        global = true;
        return this;
    }

    public ActionAPI local() {

        global = false;
        return this;
    }
}
