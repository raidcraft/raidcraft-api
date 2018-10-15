package de.raidcraft.api.action;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.trigger.TriggerListener;
import de.raidcraft.api.action.trigger.TriggerListenerConfigWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

/**
 * @author Silthus
 */
@ToString(of = {"identifier"})
@EqualsAndHashCode(of = {"identifier"})
@Data
public class TriggerFactory {

    private final TriggerManager manager;
    private final String identifier;
    private final ConfigurationSection config;
    private final Map<TriggerListener, TriggerListenerConfigWrapper> registeredListeners = new HashMap<>();
    private final List<Action<?>> actions = new ArrayList<>();
    private final List<Requirement<?>> requirements = new ArrayList<>();

    protected TriggerFactory(TriggerManager manager, String identifier, ConfigurationSection config) {

        this.manager = manager;
        this.identifier = identifier;
        this.config = config;
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<TriggerListenerConfigWrapper<T>> registerListener(@NonNull TriggerListener<T> listener) {

        if (registeredListeners.containsKey(listener)) {
            return Optional.ofNullable(registeredListeners.get(listener));
        }

        Optional<? extends TriggerListenerConfigWrapper<T>> wrapper = getManager().registerListener(listener, getIdentifier(), getConfig());
        wrapper.ifPresent(trigger -> {
            trigger.setActions(ActionAPI.filterActionTypes(actions, listener.getTriggerEntityType()));
            trigger.setRequirements(ActionAPI.filterRequirementTypes(requirements, listener.getTriggerEntityType()));
            registeredListeners.put(listener, wrapper.get());
        });

        return (Optional<TriggerListenerConfigWrapper<T>>) wrapper;
    }

    public void unregisterListener(@NonNull TriggerListener<?> listener) {

        registeredListeners.remove(listener);
        getManager().unregisterListener(listener);
    }
}
