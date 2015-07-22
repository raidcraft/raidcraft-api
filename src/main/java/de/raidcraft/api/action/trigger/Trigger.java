package de.raidcraft.api.action.trigger;

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
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
@ToString(of = {"identifier", "actions"})
@EqualsAndHashCode(of = {"identifier", "actions"})
@Data
public abstract class Trigger implements TriggerConfigGenerator {

    private String identifier;
    private final String[] actions;
    private final Map<String, List<TriggerListenerConfigWrapper<?>>> registeredListeners = new CaseInsensitiveMap<>();

    public Trigger(@NonNull String identifier, @NonNull String... actions) {

        this.identifier = identifier;
        this.actions = actions;
    }

    public final <T> void registerListener(@NonNull TriggerListener<T> listener, @NonNull String triggerIdentifier, @NonNull ConfigurationSection config) {

        if (!registeredListeners.containsKey(triggerIdentifier)) {
            registeredListeners.put(triggerIdentifier, new ArrayList<>());
        }
        registeredListeners.get(triggerIdentifier).add(new TriggerListenerConfigWrapper<>(listener, config));
    }

    public final <T> void unregisterListener(@NonNull TriggerListener<T> listener) {

        registeredListeners.values().parallelStream()
                .forEach(list -> list.removeIf(wrapper -> wrapper.getTriggerListener().equals(listener)));
    }

    protected final <T> void informListeners(@NonNull String action, @NonNull T triggeringEntity) {

        informListeners(action, triggeringEntity, config -> true);
    }

    @SuppressWarnings("unchecked")
    protected final <T> void informListeners(@NonNull String action, @NonNull T triggeringEntity, @NonNull Predicate<ConfigurationSection> predicate) {

        String identifier = getIdentifier() + "." + action;
        if (registeredListeners.containsKey(identifier)) {
            List<TriggerListenerConfigWrapper<T>> list = new ArrayList<>(registeredListeners.get(identifier)).stream()
                    .map(wrapper -> (TriggerListenerConfigWrapper<T>) wrapper)
                    .filter(wrapper -> wrapper != null && wrapper.getTriggerListener() != null)
                            // first lets check all predicates and if we can execute at all
                    .filter(wrapper -> wrapper.test(triggeringEntity, predicate))
                    .collect(Collectors.toList());
            list.stream().filter(wrapper -> wrapper.getTriggerDelay() > 0)
                    .forEach(wrapper -> Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(RaidCraftPlugin.class), () -> {
                        if (wrapper.getTriggerListener().processTrigger(triggeringEntity)) {
                            wrapper.executeActions(triggeringEntity);
                        }
                    }, wrapper.getTriggerDelay()));
            list.stream().filter(wrapper -> wrapper.getTriggerDelay() <= 0)
                    // then lets process the trigger
                    .filter(wrapper -> wrapper.getTriggerListener().processTrigger(triggeringEntity))
                    // if we get true back we are ready for action processing
                    .forEach(wrapper -> wrapper.executeActions(triggeringEntity));
        }
    }
}
