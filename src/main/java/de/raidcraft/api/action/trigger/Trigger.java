package de.raidcraft.api.action.trigger;

import de.raidcraft.api.action.ReflectionUtil;
import de.raidcraft.util.CaseInsensitiveMap;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author Silthus
 */
public abstract class Trigger {

    @Getter
    private final String identifier;
    private final Map<String, List<TriggerListenerConfigWrapper<?>>> registeredListeners = new CaseInsensitiveMap<>();

    public Trigger(String identifier) {

        this.identifier = identifier;
    }

    public final <T> void registerListener(TriggerListener<T> listener, String triggerIdentifier, ConfigurationSection config) {

        if (!registeredListeners.containsKey(triggerIdentifier)) {
            registeredListeners.put(triggerIdentifier, new ArrayList<>());
        }
        registeredListeners.get(triggerIdentifier).add(new TriggerListenerConfigWrapper(listener, config));
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
            registeredListeners.get(identifier).stream()
                    .filter(wrapper -> ReflectionUtil.genericClassMatchesType(wrapper.getClass(), triggeringEntity.getClass()))
                    .map(wrapper -> (TriggerListenerConfigWrapper<T>) wrapper)
                    .filter(wrapper -> wrapper.test(triggeringEntity, predicate))
                    .forEach(wrapper -> wrapper.getTriggerListener().processTrigger());
        }
    }
}
