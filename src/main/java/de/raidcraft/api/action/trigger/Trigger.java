package de.raidcraft.api.action.trigger;

import com.google.common.base.Strings;
import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.util.CaseInsensitiveMap;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
@ToString(of = { "identifier", "actions" })
@EqualsAndHashCode(of = { "identifier", "actions" })
@Data
public abstract class Trigger implements TriggerConfigGenerator {

    private String identifier;
    private final String[] actions;
    private final Map<String, List<TriggerListenerConfigWrapper<?>>> registeredListeners = new CaseInsensitiveMap<>();

    public Trigger(@NonNull String identifier, @NonNull String... actions) {

        this.identifier = identifier;
        this.actions = actions;
    }

    public final <T> TriggerListenerConfigWrapper<T> registerListener(@NonNull TriggerListener<T> listener, @NonNull String triggerIdentifier,
                                                                      @NonNull ConfigurationSection config) {

        if (!registeredListeners.containsKey(triggerIdentifier)) {
            registeredListeners.put(triggerIdentifier, new ArrayList<>());
        }
        TriggerListenerConfigWrapper<T> wrapper = new TriggerListenerConfigWrapper<>(triggerIdentifier, listener, config);
        registeredListeners.get(triggerIdentifier).add(wrapper);
        return wrapper;
    }

    public final <T> void unregisterListener(@NonNull TriggerListener<T> listener) {

        registeredListeners.values().parallelStream()
                .forEach(list -> list.removeIf(wrapper -> wrapper.getTriggerListener().equals(listener)));
    }

    protected final <T> void informListeners(@NonNull T triggeringEntity) {
        informListeners(null, triggeringEntity);
    }

    protected final <T> void informListeners(@NonNull T triggeringEntity, @NonNull Predicate<ConfigurationSection> predicate) {
        informListeners(null, triggeringEntity, predicate);
    }

    protected final <T> void informListeners(String action, @NonNull T triggeringEntity) {

        informListeners(action, triggeringEntity, config -> true);
    }

    protected final <T> void informListeners(String action, @NonNull T triggeringEntity,
                                             @NonNull Predicate<ConfigurationSection> predicate) {
        informListeners(action, triggeringEntity, predicate, null);
    }

    @SuppressWarnings("unchecked")
    protected final <T> void informListeners(String action, @NonNull T triggeringEntity,
                                             @NonNull Predicate<ConfigurationSection> predicate, Consumer<ConfigurationSection> onTrigger) {

        RaidCraftPlugin plugin = RaidCraft.getComponent(RaidCraftPlugin.class);
        String identifier = Strings.isNullOrEmpty(action) ? getIdentifier() :  getIdentifier() + "." + action;
        if (plugin.getConfig().debugTrigger && !plugin.getConfig().excludedTrigger.contains(identifier)) {
            plugin.getLogger().info("TRIGGER " + identifier + " fired for " + triggeringEntity);
        }
        if (registeredListeners.containsKey(identifier)) {
            if (plugin.getConfig().debugTrigger && !plugin.getConfig().excludedTrigger.contains(identifier)) {
                plugin.getLogger().info("TRIGGER " + identifier + " VALID");
            }
            List<TriggerListenerConfigWrapper<T>> list = new ArrayList<>(registeredListeners.get(identifier)).stream()
                    .map(wrapper -> (TriggerListenerConfigWrapper<T>) wrapper)
                    .filter(wrapper -> wrapper != null && wrapper.getTriggerListener() != null)
                    .filter(wrapper -> wrapper.getTriggerListener().getEntity().map(entity -> entity.equals(triggeringEntity)).orElse(true))
                    // first lets check all predicates and if we can execute at all
                    .filter(wrapper -> wrapper.test(triggeringEntity, predicate)).collect(Collectors.toList());
            if (plugin.getConfig().debugTrigger && !list.isEmpty() && !plugin.getConfig().excludedTrigger.contains(identifier)) {
                plugin.getLogger().info("TRIGGER " + identifier + " MATCHED TARGETS");
            }
            list.stream().filter(wrapper -> wrapper.getTriggerDelay() > 0)
                    .forEach(wrapper -> Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (wrapper.getTriggerListener().processTrigger(triggeringEntity, wrapper)) {
                            wrapper.executeActions(triggeringEntity);
                        }
                    }, wrapper.getTriggerDelay()));
            list.stream().filter(wrapper -> wrapper.getTriggerDelay() <= 0)
                    // then lets process the trigger
                    .filter(wrapper -> wrapper.getTriggerListener().processTrigger(triggeringEntity, wrapper))
                    // if we get true back we are ready for action processing
                    .forEach(wrapper -> {
                        if (onTrigger != null) onTrigger.accept(wrapper.getArgs());
                        wrapper.executeActions(triggeringEntity);
                    });
        }
    }
}
