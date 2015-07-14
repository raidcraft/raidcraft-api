package de.raidcraft.api.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.flow.Flow;
import de.raidcraft.api.config.builder.ConfigBuilder;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.ConfigUtil;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public final class ActionFactory<T> {

    @Getter
    private final Class<T> type;
    private final Map<String, Action<T>> actions = new CaseInsensitiveMap<>();
    // key -> alias, value -> action identifier
    private final Map<String, String> actionAliases = new CaseInsensitiveMap<>();

    protected ActionFactory(Class<T> type) {

        this.type = type;
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
        ConfigBuilder.registerInformation(action);
        RaidCraft.info("registered global action: " + identifier, "action.global");
        return this;
    }

    public ActionFactory registerAction(@NonNull JavaPlugin plugin, @NonNull String identifier, @NonNull Action<T> action) {

        identifier = plugin.getName() + "." + identifier;
        if (actions.containsKey(identifier)) {
            RaidCraft.LOGGER.warning("Action '" + identifier + "' is already registered!" + plugin.getName() + " tried to register duplicate!");
        }
        actions.put(identifier, action);
        ConfigBuilder.registerInformation(action);
        RaidCraft.info("registered action: " + identifier, "action." + plugin.getName());
        return this;
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

        return actions.keySet().contains(actionId) || actionAliases.values().stream().collect(Collectors.toList()).contains(actionId);
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
                RaidCraft.LOGGER.warning("unknown action: " + identifier + " in " + ConfigUtil.getFileName(config));
                return Optional.empty();
            }
        }
        return Optional.of(new ActionConfigWrapper<>(actions.get(identifier), config, getType()));
    }

    public Collection<Action<T>> createActions(ConfigurationSection actions) {

        ArrayList<Action<T>> list = new ArrayList<>();
        if (actions == null) {
            return list;
        }
        // lets parse our flow actions and add them
        list.addAll(Flow.parseActions(actions, getType()));

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