package de.raidcraft.api.action.trigger;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.action.trigger.global.PlayerTrigger;
import de.raidcraft.util.CaseInsensitiveMap;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

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
        registerGlobalTrigger();
    }

    private void registerGlobalTrigger() {

        registerTrigger(new PlayerTrigger());
    }

    private void registerTrigger(@NonNull Trigger trigger) {

        registeredTrigger.put(trigger.getIdentifier(), trigger);
        if (trigger instanceof Listener) {
            RaidCraft.getComponent(RaidCraftPlugin.class).registerEvents((Listener) trigger);
        }
    }

    @SneakyThrows
    public void registerTrigger(@NonNull JavaPlugin plugin, @NonNull Trigger trigger) {

        String identifier = plugin.getName() + "." + trigger.getIdentifier();
        if (registeredTrigger.containsKey(identifier)) {
            throw new TriggerException("Trigger '" + identifier + "' is already registered!");
        }
        registeredTrigger.put(identifier, trigger);
        if (trigger instanceof Listener) {
            RaidCraft.getComponent(RaidCraftPlugin.class).registerEvents((Listener) trigger);
        }
    }

    public void unregisterTrigger(@NonNull JavaPlugin plugin, @NonNull String identifier) {

        Trigger trigger = registeredTrigger.remove(identifier);
        if (trigger == null) trigger = registeredTrigger.remove(plugin.getName() + "." + identifier);
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

    public <T> void registerListener(TriggerListener<T> listener, String triggerIdentifier, ConfigurationSection config) {

        String id = triggerIdentifier.toLowerCase();
        // we need to check partial names because actions are not listed in the map
        registeredTrigger.keySet().stream()
                .filter(key -> key.startsWith(id))
                .forEach(key -> registeredTrigger.get(key).registerListener(listener, id, config));
    }

    public <T> void unregisterListener(TriggerListener<T> listener) {

        registeredTrigger.values().forEach(trigger -> trigger.unregisterListener(listener));
    }
}
