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
    private final Map<T, Integer> counters = new HashMap<>();

    protected RequirementConfigWrapper(Requirement<T> requirement, ConfigurationSection config) {

        this.requirement = requirement;
        this.config = config;
    }

    @Override
    public ConfigurationSection getConfig() {

        return this.config;
    }

    @Override
    public int getCount(T entity) {

        return counters.getOrDefault(entity, 0);
    }

    @Override
    public boolean test(T entity) {

        boolean successfullyChecked = successfulChecks.getOrDefault(entity, false);

        if (isPersistant() && successfullyChecked) return true;

        successfullyChecked = requirement.test(entity);

        if (successfullyChecked) counters.put(entity, counters.getOrDefault(entity, 0) + 1);

        successfulChecks.put(entity, successfullyChecked);

        if (isCounting()) {
            if (hasCountText() && entity instanceof Player) {
                ((Player) entity).sendMessage(getCountText(entity));
            }
            return getRequiredCount() <= getCount(entity);
        }
        return successfullyChecked;
    }

    @Override
    public int compareTo(Requirement<T> other) {

        return Integer.compare(getOrder(), other.getOrder());
    }
}
