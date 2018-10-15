package de.raidcraft.api.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.action.flow.Flow;
import de.raidcraft.api.action.trigger.Trigger;
import de.raidcraft.api.action.trigger.TriggerGroup;
import de.raidcraft.api.action.trigger.TriggerListener;
import de.raidcraft.api.action.trigger.TriggerListenerConfigWrapper;
import de.raidcraft.api.config.builder.ConfigBuilder;
import de.raidcraft.api.config.builder.ConfigGenerator;
import de.raidcraft.util.CaseInsensitiveMap;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * @author Silthus
 */
public final class TriggerManager implements Listener {

    private final Map<String, Trigger> registeredTrigger = new CaseInsensitiveMap<>();
    private final Map<String, ConfigGenerator.Information> triggerInformation = new CaseInsensitiveMap<>();
    // key -> alias, value -> trigger identifier
    private final Map<String, String> aliases = new CaseInsensitiveMap<>();

    protected TriggerManager() {
        RaidCraft.registerEvents(this, RaidCraft.getComponent(RaidCraftPlugin.class));
    }

    public boolean contains(String identifier) {

        return registeredTrigger.containsKey(identifier) || aliases.containsKey(identifier);
    }

    public Optional<ConfigGenerator.Information> getInformation(String identifier) {

        if (triggerInformation.containsKey(identifier)) {
            return Optional.of(triggerInformation.get(identifier));
        }
        if (aliases.containsKey(identifier)) {
            return Optional.ofNullable(triggerInformation.get(aliases.get(identifier)));
        }
        return Optional.empty();
    }

    public TriggerManager registerGlobalTrigger(@NonNull Trigger trigger) {

        for (String action : trigger.getActions()) {
            registeredTrigger.put(trigger.getIdentifier() + "." + action, trigger);
        }

        if (trigger.getActions().length < 1) {
            registeredTrigger.put(trigger.getIdentifier(), trigger);
        }

        ConfigBuilder.getInformations(trigger).forEach(information -> {
            triggerInformation.put(information.value(), information);
            Arrays.stream(information.aliases()).forEach(alias -> aliases.put(alias, information.value()));
        });
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
            registeredTrigger.put(triggerName, trigger);
        }

        if (trigger.getActions().length < 1) {
            registeredTrigger.put(identifier, trigger);
        }

        ConfigBuilder.getInformations(trigger).forEach(information -> {
            triggerInformation.put(plugin.getName() + "." + information.value(), information);
            Arrays.stream(information.aliases()).forEach(alias -> aliases.put(alias, plugin.getName() + "." + information.value()));
        });
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

    public <T> Optional<TriggerListenerConfigWrapper<T>> registerListener(@NonNull TriggerListener<T> listener, @NonNull String triggerIdentifier, @NonNull ConfigurationSection config) {

        // we need to check partial names because actions are not listed in the map
        if (!registeredTrigger.containsKey(triggerIdentifier)) {
            if (!aliases.containsKey(triggerIdentifier)) {
                return Optional.empty();
            } else {
                triggerIdentifier = aliases.get(triggerIdentifier);
            }
        }
        Trigger trigger = registeredTrigger.get(triggerIdentifier);
        if (trigger != null) {
            return Optional.of(trigger.registerListener(listener, triggerIdentifier, config));
        }
        return Optional.empty();
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

    public List<TriggerFactory> createTriggerFactories(ConfigurationSection trigger) {

        return Flow.parseTrigger(trigger);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        registeredTrigger.values().stream()
                .filter(trigger -> trigger instanceof TriggerGroup)
                .forEach(trigger -> ((TriggerGroup) trigger).registerPlayer(event.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        registeredTrigger.values().stream()
                .filter(trigger -> trigger instanceof TriggerGroup)
                .forEach(trigger -> ((TriggerGroup) trigger).unregisterPlayer(event.getPlayer()));
    }
}
