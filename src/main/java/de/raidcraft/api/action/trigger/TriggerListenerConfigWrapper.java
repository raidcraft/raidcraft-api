package de.raidcraft.api.action.trigger;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.ReflectionUtil;
import de.raidcraft.api.action.action.Action;
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
                .map(key -> RaidCraft.getComponent(ActionFactory.class)
                        .create(section.getString(key + ".type"), section.getConfigurationSection(key)))
                .filter(action -> action.matchesType(getTriggerListener().getTriggerEntityType().getClass()))
                .map(action -> (Action<T>) action)
                .collect(Collectors.toList());
    }

    public boolean matchesType(Class<?> type) {

        try {
            RaidCraft.LOGGER.info("matchesType called");
            return ReflectionUtil.isMatchingGenericMethodType(
                    getClass().getMethod("test", getTriggerListener().getTriggerEntityType().getClass(), Predicate.class),
                    type
            );
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    protected boolean test(T triggeringEntity, Predicate<ConfigurationSection> predicate) {

        RaidCraft.LOGGER.info("testing trigger: " + predicate);
        if (triggerListener.getTriggerEntityType().equals(triggeringEntity) && predicate.test(config)) {
            // trigger our actions
            actions.forEach(action -> action.accept(triggeringEntity));
            RaidCraft.LOGGER.info("success!");
            return true;
        }
        return false;
    }
}
