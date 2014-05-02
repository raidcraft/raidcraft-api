package de.raidcraft.api.action.action;

import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
@Data
class ActionConfigWrapper<T> implements Action<T> {

    private final Action<T> action;
    private final ConfigurationSection config;

    protected ActionConfigWrapper(Action<T> action, ConfigurationSection config) {

        this.action = action;
        this.config = config;
    }

    @Override
    public ConfigurationSection getConfig() {

        return this.config;
    }

    @Override
    public void accept(T type) {

        action.accept(type);
    }
}
