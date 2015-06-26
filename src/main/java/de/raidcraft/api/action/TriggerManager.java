package de.raidcraft.api.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.action.flow.Flow;
import de.raidcraft.api.action.trigger.Trigger;
import de.raidcraft.api.action.trigger.TriggerListener;
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
import java.util.Optional;

/**
 * @author Silthus
 */
public final class TriggerManager {

    private final Map<String, Trigger> registeredTrigger = new CaseInsensitiveMap<>();

    protected TriggerManager() {

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

    public <T> Optional<Trigger> registerListener(@NonNull TriggerListener<T> listener, @NonNull String triggerIdentifier, @NonNull ConfigurationSection config) {

        String id = triggerIdentifier.toLowerCase();
        // we need to check partial names because actions are not listed in the map
        Trigger trigger = registeredTrigger.get(id);
        if (trigger != null) {
            trigger.registerListener(listener, id, config);
        }
        return Optional.ofNullable(trigger);
    }

    public <T> void unregisterListener(@NonNull TriggerListener<T> listener) {

        registeredTrigger.values().forEach(trigger -> trigger.unregisterListener(listener));
    }

    public TriggerFactory createTrigger(@NonNull String identifier, @NonNull ConfigurationSection config) {

        return new TriggerFactory(this, identifier, config);
    }

    public Map<String, Trigger> getTrigger() {

        return new HashMap<>(registeredTrigger);
    }

    public Collection<TriggerFactory> createTriggerFactories(ConfigurationSection trigger) {

        List<TriggerFactory> list = new ArrayList<>();
        // parse our flow trigger
        list.addAll(Flow.parseTrigger(trigger));

        if (trigger != null) {
            for (String key : trigger.getKeys(false)) {
                // handle via flow api
                if (trigger.isList(key)) continue;
                list.add(createTrigger(trigger.getString(key + ".type"), trigger.getConfigurationSection(key)));
            }
        }
        return list;
    }
}
