package de.raidcraft.api.action.requirement;

import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
@Data
class RequirementConfigWrapper<T> implements Requirement<T>, Comparable<RequirementConfigWrapper<T>> {

    private final Requirement<T> requirement;
    private final ConfigurationSection config;
    private final boolean persistant;
    private final int order;
    private boolean successfullyChecked = false;

    protected RequirementConfigWrapper(Requirement<T> requirement, ConfigurationSection config) {

        this.requirement = requirement;
        this.config = config;
        this.persistant = config.getBoolean("persistant", false);
        this.order = config.getInt("order", 0);
    }

    @Override
    public ConfigurationSection getConfig() {

        return this.config;
    }

    @Override
    public boolean test(T t) {

        if (isPersistant() && successfullyChecked) return true;
        successfullyChecked = requirement.test(t);
        return successfullyChecked;
    }

    @Override
    public int compareTo(RequirementConfigWrapper<T> other) {

        return Integer.compare(getOrder(), other.getOrder());
    }
}
