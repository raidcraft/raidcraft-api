package de.raidcraft.api.action.requirement;

import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
@Data
class RequirementConfigWrapper<T> implements Requirement<T> {

    private final Requirement<T> requirement;
    private final ConfigurationSection config;

    protected RequirementConfigWrapper(Requirement<T> requirement, ConfigurationSection config) {

        this.requirement = requirement;
        this.config = config;
    }

    @Override
    public ConfigurationSection getConfig() {

        return this.config;
    }

    @Override
    public boolean test(T t) {

        return requirement.test(t);
    }
}
