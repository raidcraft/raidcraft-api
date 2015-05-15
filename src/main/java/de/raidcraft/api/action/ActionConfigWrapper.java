package de.raidcraft.api.action;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.RevertableAction;
import de.raidcraft.api.action.requirement.Requirement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
@EqualsAndHashCode(of = {"action", "config"})
@Data
class ActionConfigWrapper<T> implements RevertableAction<T> {

    private final Class<T> type;
    private final Action<T> action;
    private final ConfigurationSection config;
    private List<Requirement<T>> requirements = new ArrayList<>();

    protected ActionConfigWrapper(Action<T> action, ConfigurationSection config, Class<T> type) {

        this.type = type;
        this.action = action;
        this.config = config;
        this.requirements = ActionAPI.createRequirements(getIdentifier(), config.getConfigurationSection("requirements"), type);
    }

    public ConfigurationSection getConfig() {

        ConfigurationSection args = this.config.getConfigurationSection("args");
        if (args == null) args = this.config.createSection("args");
        return args;
    }

    public void accept(T type) {

        accept(type, getConfig());
    }

    public void accept(T type, ConfigurationSection config) {

        if (!requirements.isEmpty()) {
            boolean allMatch = requirements.stream()
                    .allMatch(requirement -> requirement.test(type));
            if (!allMatch) return;
        }
        action.accept(type, config);
    }

    @Override
    public void revert(T type) {

        revert(type, getConfig());
    }

    @Override
    public void revert(T type, ConfigurationSection config) {

        if (action instanceof RevertableAction) {
            ((RevertableAction<T>) action).revert(type, config);
        }
    }
}
