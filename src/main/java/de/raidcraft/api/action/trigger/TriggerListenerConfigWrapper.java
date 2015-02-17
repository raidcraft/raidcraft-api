package de.raidcraft.api.action.trigger;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.ActionException;
import de.raidcraft.api.action.action.ActionFactory;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.requirement.RequirementException;
import de.raidcraft.api.action.requirement.RequirementFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * @author Silthus
 */
@ToString(of = {"triggerListener", "config"})
@EqualsAndHashCode(of = {"triggerListener", "config"})
@Data
class TriggerListenerConfigWrapper<T> {

    private final TriggerListener<T> triggerListener;
    private final ConfigurationSection config;
    private Collection<Action<T>> actions = new ArrayList<>();
    private Collection<Requirement<T>> requirements = new ArrayList<>();

    protected TriggerListenerConfigWrapper(TriggerListener<T> triggerListener, ConfigurationSection config) {

        this.triggerListener = triggerListener;
        this.config = config;
        try {
            this.actions = RaidCraft.getComponent(ActionFactory.class)
                    .createActions(config.getConfigurationSection("actions"), getTriggerListener().getTriggerEntityType());
            this.requirements = RaidCraft.getComponent(RequirementFactory.class)
                    .createRequirements(triggerListener.getListenerId(),
                            config.getConfigurationSection("requirements"),
                            getTriggerListener().getTriggerEntityType());
        } catch (ActionException | RequirementException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
    }

    protected boolean test(T triggeringEntity, Predicate<ConfigurationSection> predicate) {

        ConfigurationSection args = config.getConfigurationSection("args");
        if (args == null) args = config.createSection("args");
        return triggerListener.getTriggerEntityType().isInstance(triggeringEntity)
                && predicate.test(args)
                && requirements.stream().allMatch(requirement -> requirement.test(triggeringEntity));
    }

    protected void executeActions(T triggeringEntity) {

        actions.forEach(action -> action.accept(triggeringEntity));
    }
}
