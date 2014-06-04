package de.raidcraft.api.action.requirement;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
@EqualsAndHashCode(of = {"requirement", "config"})
@Data
class RequirementConfigWrapper<T> implements Requirement<T>, Comparable<Requirement<T>> {

    private final Requirement<T> requirement;
    private final ConfigurationSection config;
    private int count;
    private boolean successfullyChecked = false;

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

        if (isPersistant() && successfullyChecked) return true;
        if (isCounting() && getRequiredCount() <= count) return true;
        successfullyChecked = requirement.test(t);
        if (successfullyChecked) count++;
        return (isCounting() && getRequiredCount() <= count) || successfullyChecked;
    }

    @Override
    public int compareTo(Requirement<T> other) {

        return Integer.compare(getOrder(), other.getOrder());
    }
}
