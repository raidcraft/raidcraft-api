package de.raidcraft.api.action.requirement;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
@EqualsAndHashCode(of = {"requirement", "config"})
@Data
class RequirementConfigWrapper<T> implements Requirement<T>, Comparable<Requirement<T>> {

    private final Requirement<T> requirement;
    private final ConfigurationSection config;
    private final Map<T, Boolean> successfulChecks = new HashMap<>();
    private int count;

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

        boolean successfullyChecked = successfulChecks.getOrDefault(t, false);

        if (isPersistant() && successfullyChecked) return true;

        successfullyChecked = requirement.test(t);

        if (successfullyChecked) count++;

        successfulChecks.put(t, successfullyChecked);

        if (isCounting()) {
            if (hasCountText() && t instanceof Player) {
                ((Player) t).sendMessage(getCountText());
            }
            return getRequiredCount() <= getCount();
        }
        return successfullyChecked;
    }

    @Override
    public int compareTo(Requirement<T> other) {

        return Integer.compare(getOrder(), other.getOrder());
    }
}
