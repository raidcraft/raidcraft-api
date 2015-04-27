package de.raidcraft.api.action.trigger;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.config.builder.ConfigBuilder;
import de.raidcraft.util.CaseInsensitiveMap;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public final class TriggerManager implements Component {

    private static final TriggerManager INSTANCE = new TriggerManager();

    @NonNull
    public static TriggerManager getInstance() {

        return INSTANCE;
    }

    private final Map<String, Trigger> registeredTrigger = new CaseInsensitiveMap<>();

    private TriggerManager() {

        RaidCraft.registerComponent(TriggerManager.class, this);
        ActionAPI.registerGlobalTrigger(this);
    }

    public TriggerManager registerGlobalTrigger(@NonNull Trigger trigger) {

        for (String action : trigger.getActions()) {
            registeredTrigger.put(trigger.getIdentifier() + "." + action, trigger);
            ConfigBuilder.registerInformation(trigger);
        }
        if (trigger instanceof Listener) {
            RaidCraft.getComponent(RaidCraftPlugin.class).registerEvents((Listener) trigger);
        }
        return this;
    }

    public TriggerManager registerTrigger(@NonNull JavaPlugin plugin, @NonNull Trigger trigger) {

        String triggerName;
        String identifier = plugin.getName() + "." + trigger.getIdentifier();
        trigger.setIdentifier(identifier);
        for (String action : trigger.getActions()) {
            triggerName = identifier + "." + action;
            if (registeredTrigger.containsKey(triggerName)) {
                RaidCraft.LOGGER.warning("duplicate trigger found: " + triggerName);
                continue;
            }
            registeredTrigger.put(triggerName, trigger);
            ConfigBuilder.registerInformation(trigger);
        }
        if (trigger instanceof Listener) {
            RaidCraft.getComponent(RaidCraftPlugin.class).registerEvents((Listener) trigger);
        }
        return this;
    }

    public void unregisterTrigger(@NonNull JavaPlugin plugin, @NonNull String identifier) {

        Trigger trigger = registeredTrigger.remove(identifier);
        if (trigger == null) trigger = registeredTrigger.remove(plugin.getName() + "." + identifier);
        if (trigger == null) {
            registeredTrigger.entrySet().forEach(entry -> {
                if (entry.getKey().startsWith(identifier.toLowerCase())) {
                    registeredTrigger.remove(entry.getKey());
                    if (entry.getValue() instanceof Listener) {
                        HandlerList.unregisterAll((Listener) entry.getValue());
                    }
                }
            });
        }
        if (trigger != null) {
            if (trigger instanceof Listener) HandlerList.unregisterAll((Listener) trigger);
            RaidCraft.LOGGER.info("removed trigger: " + identifier + " (" + plugin.getName() + ")");
        }
    }

    public void unregisterTrigger(@NonNull JavaPlugin plugin) {

        registeredTrigger.keySet().stream()
                .filter(key -> key.startsWith(plugin.getName().toLowerCase()))
                .forEach(key -> {
                    Trigger trigger = registeredTrigger.remove(key);
                    if (trigger != null && trigger instanceof Listener) HandlerList.unregisterAll((Listener) trigger);
                });
        RaidCraft.LOGGER.info("removed all trigger of: " + plugin.getName());
    }

    public <T> void registerListener(@NonNull TriggerListener<T> listener, @NonNull String triggerIdentifier, @NonNull ConfigurationSection config) {

        String id = triggerIdentifier.toLowerCase();
        // we need to check partial names because actions are not listed in the map
        Trigger trigger = registeredTrigger.get(id);
        if (trigger != null) {
            trigger.registerListener(listener, id, config);
        }
    }

    public <T> void unregisterListener(@NonNull TriggerListener<T> listener) {

        registeredTrigger.values().forEach(trigger -> trigger.unregisterListener(listener));
    }

    public TriggerFactory getTrigger(@NonNull String identifier, @NonNull ConfigurationSection config) {

        return new TriggerFactory(this, identifier, config);
    }

    public Map<String, Trigger> getTrigger() {

        return new HashMap<>(registeredTrigger);
    }

    public Collection<TriggerFactory> createTriggerFactories(ConfigurationSection trigger) {

        List<TriggerFactory> list = new ArrayList<>();
        if (trigger != null) {
            list = trigger.getKeys(false).stream()
                    .map(key -> TriggerManager.getInstance().getTrigger(trigger.getString(key + ".type"), trigger.getConfigurationSection(key)))
                    .collect(Collectors.toList());
        }
        return list;
    }
}
