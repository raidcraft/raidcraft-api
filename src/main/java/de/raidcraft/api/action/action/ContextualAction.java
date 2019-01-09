package de.raidcraft.api.action.action;

import de.raidcraft.api.action.ActionConfigWrapper;
import org.bukkit.configuration.ConfigurationSection;

public interface ContextualAction<T>  extends Action<T> {

    @Override
    default void accept(T type, ConfigurationSection config) {

        throw new UnsupportedOperationException("accept(T, ConfigurationSection) ist unsupported in a ContextualAction! " +
                "Use the test(T type, ActionConfigWrapper<T> context, ConfigurationSection config) method instead.");
    }

    void accept(T type, ActionConfigWrapper<T> context, ConfigurationSection config);
}
