package de.raidcraft.api.action.trigger;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.ActionException;
import de.raidcraft.api.action.action.ActionFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
@ToString(of = {"triggerListener", "config"})
@EqualsAndHashCode(of = {"triggerListener", "config"})
@Data
class TriggerListenerConfigWrapper<T> {

    private final TriggerListener<T> triggerListener;
    private final ConfigurationSection config;
    private List<Action<T>> actions = new ArrayList<>();

    protected TriggerListenerConfigWrapper(TriggerListener<T> triggerListener, ConfigurationSection config) {

        this.triggerListener = triggerListener;
        this.config = config;
        loadActions();
    }

    @SuppressWarnings("unchecked")
    private void loadActions() {

        ConfigurationSection section = config.getConfigurationSection("actions");
        if (section == null) return;
        actions = section.getKeys(false).stream()
                .map(key -> {
                    try {
                        return RaidCraft.getComponent(ActionFactory.class)
                                .create(section.getString(key + ".type"), section.getConfigurationSection(key));
                    } catch (ActionException e) {
                        RaidCraft.LOGGER.warning(e.getMessage() + " in " + getTriggerListener());
                    }
                    return null;
                })
                .filter(key -> key != null)
                .filter(action -> action.matchesType(getTriggerListener().getTriggerEntityType()))
                .map(action -> (Action<T>) action)
                .collect(Collectors.toList());
    }

    protected boolean test(T triggeringEntity, Predicate<ConfigurationSection> predicate) {

        RaidCraft.LOGGER.info("Triggering Entity (" + triggeringEntity + ") is instance of " + triggerListener.getTriggerEntityType() + ": " + triggerListener.getTriggerEntityType().isInstance(triggeringEntity));
        RaidCraft.LOGGER.info("Predicate " + predicate + " is " + predicate.test(config));
        return triggerListener.getTriggerEntityType().isInstance(triggeringEntity) && predicate.test(config);
    }

    protected void executeActions(T triggeringEntity) {

        actions.forEach(action -> action.accept(triggeringEntity));
    }
}
