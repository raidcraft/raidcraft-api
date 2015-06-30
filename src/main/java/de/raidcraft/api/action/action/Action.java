package de.raidcraft.api.action.action;

import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.requirement.Requirement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

/**
 * @author Silthus
 */
@FunctionalInterface
public interface Action<T> extends ActionConfigGenerator {

    default String getIdentifier() {

        return ActionAPI.getIdentifier(this);
    }

    default void addRequirement(Requirement<T> requirement) {

        throw new UnsupportedOperationException();
    }

    void accept(T type, ConfigurationSection config);

    default void accept(T type) {

        accept(type, new MemoryConfiguration());
    }
}
