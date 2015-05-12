package de.raidcraft.api.action.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.requirement.RequirementException;
import de.raidcraft.api.action.requirement.RequirementFactory;
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

    private final Action<T> action;
    private final ConfigurationSection config;
    private List<Requirement<?>> requirements = new ArrayList<>();

    protected ActionConfigWrapper(Action<T> action, ConfigurationSection config) {

        this.action = action;
        this.config = config;
        try {
            this.requirements = RaidCraft.getComponent(RequirementFactory.class)
                    .createRequirements(getIdentifier(), config.getConfigurationSection("requirements"));
        } catch (RequirementException e) {
            e.printStackTrace();
        }
    }

    public ConfigurationSection getConfig() {

        ConfigurationSection args = this.config.getConfigurationSection("args");
        if (args == null) args = this.config.createSection("args");
        return args;
    }

    public void accept(T type) {

        accept(type, getConfig());
    }

    @SuppressWarnings("unchecked")
    public void accept(T type, ConfigurationSection config) {

        if (!requirements.isEmpty()) {
            boolean allMatch = requirements.stream()
                    .filter(requirement -> requirement.matchesType(type.getClass()))
                    .map(requirement -> (Requirement<T>) requirement)
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
