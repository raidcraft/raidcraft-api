package de.raidcraft.api.action.trigger;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.ReflectionUtil;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.ActionFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
@EqualsAndHashCode(of = {"triggerListener", "config"})
@Data
class TriggerListenerConfigWrapper<T> {

    private final TriggerListener triggerListener;
    private final ConfigurationSection config;
    private List<Action<T>> actions = new ArrayList<>();

    protected TriggerListenerConfigWrapper(TriggerListener triggerListener, ConfigurationSection config) {

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
                .filter(action -> ReflectionUtil.genericClassMatchesType(action.getClass(), getTriggerListener().getTriggerEntityType().getClass()))
                .map(action -> (Action<T>) action)
                .collect(Collectors.toList());
    }

    protected boolean test(T triggeringEntity, Predicate<ConfigurationSection> predicate) {

        if (triggerListener.getTriggerEntityType().equals(triggeringEntity) && predicate.test(config)) {
            // trigger our actions
            actions.forEach(action -> action.accept(triggeringEntity));
            return true;
        }
        return false;
    }
}
