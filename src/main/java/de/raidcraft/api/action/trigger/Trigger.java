package de.raidcraft.api.action.trigger;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.util.CaseInsensitiveMap;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Silthus
 */
@ToString(of = {"identifier", "actions"})
@EqualsAndHashCode(of = {"identifier", "actions"})
@Data
public abstract class Trigger {

    private final String identifier;
    private final String[] actions;
    private final Map<String, List<TriggerListenerConfigWrapper<?>>> registeredListeners = new CaseInsensitiveMap<>();

    public Trigger(String identifier, String... actions) {

        this.identifier = identifier;
        this.actions = actions;
    }

    public final <T> void registerListener(TriggerListener<T> listener, String triggerIdentifier, ConfigurationSection config) {

        if (!registeredListeners.containsKey(triggerIdentifier)) {
            registeredListeners.put(triggerIdentifier, new ArrayList<>());
        }
        registeredListeners.get(triggerIdentifier).add(new TriggerListenerConfigWrapper<>(listener, config));
    }

    public final <T> void unregisterListener(TriggerListener<T> listener) {

        registeredListeners.values().parallelStream()
                .forEach(list -> list.removeIf(wrapper -> wrapper.getTriggerListener().equals(listener)));
    }

    protected final <T> void informListeners(String action, T triggeringEntity) {

        informListeners(action, triggeringEntity, config -> true);
    }

    @SuppressWarnings("unchecked")
    protected final <T> void informListeners(String action, T triggeringEntity, Predicate<ConfigurationSection> predicate) {

        String identifier = getIdentifier() + "." + action;
        if (registeredListeners.containsKey(identifier)) {
            Stream<TriggerListenerConfigWrapper<?>> stream;
            if (RaidCraft.getComponent(RaidCraftPlugin.class).getConfig().parallelActionAPI) {
                stream = registeredListeners.get(identifier).parallelStream();
            } else {
                stream = registeredListeners.get(identifier).stream();
            }
            stream/*.filter(wrapper -> wrapper.matchesType(triggeringEntity.getClass()))*/
                    .map(wrapper -> (TriggerListenerConfigWrapper<T>) wrapper)
                    .filter(wrapper -> wrapper.test(triggeringEntity, predicate))
                    .collect(Collectors.toList())
                    .forEach(wrapper -> wrapper.getTriggerListener().processTrigger());
        }
    }
}
