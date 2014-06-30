package de.raidcraft.api.action.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.config.builder.ConfigBuilder;
import de.raidcraft.util.CaseInsensitiveMap;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public final class ActionFactory implements Component {

    private static final ActionFactory INSTANCE = new ActionFactory();
    @NonNull
    public static ActionFactory getInstance() {

        return INSTANCE;
    }

    private final Map<String, Action<?>> actions = new CaseInsensitiveMap<>();

    private ActionFactory() {

        RaidCraft.registerComponent(ActionFactory.class, this);
        ActionAPI.registerGlobalActions(this);
    }

    public <T> void registerGlobalAction(@NonNull String identifier, @NonNull Action<T> action) {

        actions.put(identifier, action);
        ConfigBuilder.registerConfigGenerator(action);
        RaidCraft.LOGGER.info("registered global action: " + identifier);
    }

    @SneakyThrows
    public <T> void registerAction(@NonNull JavaPlugin plugin, @NonNull String identifier, @NonNull Action<T> action) {

        identifier = plugin.getName() + "." + identifier;
        if (actions.containsKey(identifier)) {
            throw new ActionException("Action '" + identifier + "' is already registered!");
        }
        actions.put(identifier, action);
        ConfigBuilder.registerConfigGenerator(action);
        RaidCraft.LOGGER.info("registered action: " + identifier);
    }

    public void unregisterAction(@NonNull JavaPlugin plugin, @NonNull String identifier) {

        Action<?> action = actions.remove(identifier);
        if (action == null) action = actions.remove(plugin.getName() + "." + identifier);
        if (action != null) {
            RaidCraft.LOGGER.info("removed action: " + identifier + " (" + plugin.getName() + ")");
        }
    }

    public void unregisterActions(@NonNull JavaPlugin plugin) {

        actions.keySet().removeIf(key -> key.startsWith(plugin.getName().toLowerCase()));
        RaidCraft.LOGGER.info("removed all actions of: " + plugin.getName());
    }

    public Map<String, Action<?>> getActions() {

        return new HashMap<>(actions);
    }

    public String getActionIdentifier(Action<?> action) {

        for (Map.Entry<String, Action<?>> entry : actions.entrySet()) {
            if (entry.getValue().equals(action)) {
                return entry.getKey();
            }
        }
        return "undefined";
    }

    @SneakyThrows
    public Action<?> create(@NonNull String identifier, @NonNull ConfigurationSection config) {

        if (!actions.containsKey(identifier)) {
            throw new ActionException("unknown action: " + identifier);
        }
        return new ActionConfigWrapper<>(actions.get(identifier), config);
    }

    public Collection<Action<?>> createActions(ConfigurationSection actions) {

        if (actions == null) return new ArrayList<>();
        return actions.getKeys(false).stream()
                .map(key -> create(actions.getString(key + ".type"), actions.getConfigurationSection(key)))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public <T> Collection<Action<T>> createActions(ConfigurationSection actions, Class<T> type) {

        return createActions(actions).stream()
                .filter(action -> action.matchesType(type))
                .map(action -> (Action<T>) action)
                .collect(Collectors.toList());
    }
}