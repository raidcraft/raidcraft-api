package de.raidcraft.api.action.requirement;

import de.raidcraft.api.action.RequirementConfigWrapper;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author mdoering
 */
public interface ContextualRequirement<T> extends Requirement<T> {

    @Override
    default boolean test(T type, ConfigurationSection config) {

        throw new UnsupportedOperationException("test(T, ConfigurationSection) ist unsupported in a ContextualRequirement! " +
                "Use the test(T type, RequirementConfigWrapper<T> context, ConfigurationSection config) method instead.");
    }

    boolean test(T type, RequirementConfigWrapper<T> context, ConfigurationSection config);
}
