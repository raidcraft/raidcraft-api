package de.raidcraft.api.action.requirement;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
@EqualsAndHashCode(of = {"requirement", "config"})
@Data
class RequirementConfigWrapper<T> implements Requirement<T>, Comparable<RequirementConfigWrapper<T>> {

    private final Requirement<T> requirement;
    private final ConfigurationSection config;
    private final boolean persistant;
    private final int order;
    private final int requiredCount;
    private int count;
    private boolean successfullyChecked = false;

    protected RequirementConfigWrapper(Requirement<T> requirement, ConfigurationSection config) {

        this.requirement = requirement;
        this.config = config;
        this.persistant = config.getBoolean("persistant", false);
        this.order = config.getInt("order", 0);
        this.requiredCount = config.getInt("count", 0);
    }

    @Override
    public ConfigurationSection getConfig() {

        return this.config;
    }

    @Override
    public boolean test(T t) {

        if (isPersistant() && successfullyChecked) return true;
        if (requiredCount > 0 && requiredCount <= count) return true;
        successfullyChecked = requirement.test(t);
        if (successfullyChecked) count++;
        return (requiredCount > 0 && requiredCount <= count) || successfullyChecked;
    }

    @Override
    public int compareTo(RequirementConfigWrapper<T> other) {

        return Integer.compare(getOrder(), other.getOrder());
    }
}
