package de.raidcraft.api.action.action;

import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.flow.FlowExpression;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

/**
 * @author Silthus
 */
@FunctionalInterface
public interface Action<T> extends ActionConfigGenerator, FlowExpression {

    default String getIdentifier() {

        return ActionAPI.getIdentifier(this);
    }

    void accept(T type, ConfigurationSection config);

    default void accept(T type) {

        accept(type, new MemoryConfiguration());
    }
}
