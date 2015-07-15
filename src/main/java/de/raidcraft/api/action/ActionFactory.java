package de.raidcraft.api.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.flow.Flow;
import de.raidcraft.api.config.builder.ConfigBuilder;
import de.raidcraft.api.config.builder.ConfigGenerator;
import de.raidcraft.util.CaseInsensitiveMap;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Silthus
 */
public final class ActionFactory<T> {

    @Getter
    private final Class<T> type;
    private final Map<String, Action<T>> actions = new CaseInsensitiveMap<>();
    private final Map<String, ConfigGenerator.Information> actionInformation = new CaseInsensitiveMap<>();
    // key -> alias, value -> action identifier
    private final Map<String, String> actionAliases = new CaseInsensitiveMap<>();

    protected ActionFactory(Class<T> type) {

        this.type = type;
    }

    public Optional<ConfigGenerator.Information> getInformation(String identifier) {

        if (actionInformation.containsKey(identifier)) {
            return Optional.of(actionInformation.get(identifier));
        }
        if (actionAliases.containsKey(identifier)) {
            return Optional.ofNullable(actionInformation.get(actionAliases.get(identifier)));
        }
        return Optional.empty();
    }

    public ActionFactory addAlias(String action, String alias) {

        actionAliases.put(alias, action);
        return this;
    }

    public ActionFactory addAlias(BasePlugin plugin, String action, String alias) {

        return addAlias(plugin.getName().toLowerCase() + "." + action, alias);
    }

    public ActionFactory registerGlobalAction(@NonNull String identifier, @NonNull Action<T> action) {

        actions.put(identifier, action);
        Optional<ConfigGenerator.Information> information = ConfigBuilder.getInformation(action);
        if (information.isPresent()) {
            actionInformation.put(identifier, information.get());
        } else {
            RaidCraft.LOGGER.warning("no @Information defined for action " + identifier);
        }
        RaidCraft.info("registered action: " + identifier, "action");
        return this;
    }

    public ActionFactory registerAction(@NonNull JavaPlugin plugin, @NonNull String identifier, @NonNull Action<T> action) {

        identifier = plugin.getName() + "." + identifier;
        return registerGlobalAction(identifier, action);
    }

    public void unregisterAction(@NonNull JavaPlugin plugin, @NonNull String identifier) {

        Action<T> requirement = actions.remove(identifier);
        if (requirement == null) requirement = actions.remove(plugin.getName() + "." + identifier);
        if (requirement != null) {
            RaidCraft.info("removed action: " + identifier + " (" + plugin.getName() + ")", "action." + plugin.getName());
        }
    }

    public void unregisterActions(@NonNull JavaPlugin plugin) {

        actions.keySet().stream()
                .filter(key -> key.startsWith(plugin.getName().toLowerCase()))
                .forEach(actions::remove);
        RaidCraft.info("removed all actions of: " + plugin.getName(), "action." + plugin.getName());
    }

    public Map<String, Action<T>> getActions() {

        return new CaseInsensitiveMap<>(actions);
    }

    public boolean contains(Action action) {

        return actions.values().contains(action);
    }

    public boolean contains(String actionId) {

        return actions.containsKey(actionId) || actionAliases.containsKey(actionId);
    }

    public Optional<String> getActionIdentifier(Action action) {

        for (Map.Entry<String, Action<T>> entry : actions.entrySet()) {
            if (entry.getValue().equals(action)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    public Optional<Action<T>> create(@NonNull String identifier, @NonNull ConfigurationSection config) {

        if (!actions.containsKey(identifier)) {
            // lets see if we find a matching alias
            if (actionAliases.containsKey(identifier) && actions.containsKey(actionAliases.get(identifier))) {
                identifier = actionAliases.get(identifier);
            } else {
                ActionAPI.UNKNOWN_ACTIONS.add(identifier);
                return Optional.empty();
            }
        }
        return Optional.of(new ActionConfigWrapper<>(actions.get(identifier), config, getType()));
    }

    @SuppressWarnings("unchecked")
    public Collection<Action<T>> createActions(ConfigurationSection actions) {

        ArrayList<Action<T>> list = new ArrayList<>();
        if (actions == null) {
            return list;
        }
        // lets parse our flow actions and add them
        List<Action<?>> flowActions = Flow.parseActions(actions);
        flowActions.stream()
                .filter(action -> ActionAPI.matchesType(action, getType()))
                .map(action -> (Action<T>) action)
                .forEach(list::add);

        for (String key : actions.getKeys(false)) {
            // lists are handled by the flow parser
            if (actions.isList(key)) continue;
            Optional<Action<T>> optional = create(actions.getString(key + ".type"), actions.getConfigurationSection(key));
            if (optional.isPresent()) {
                list.add(optional.get());
            }
        }
        return list;
    }
}