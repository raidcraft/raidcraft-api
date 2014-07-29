package de.raidcraft.api.action.action;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
@EqualsAndHashCode(of = {"action", "config"})
@Data
class ActionConfigWrapper<T> implements Action<T> {

    private final Action<T> action;
    private final ConfigurationSection config;

    protected ActionConfigWrapper(Action<T> action, ConfigurationSection config) {

        this.action = action;
        this.config = config;
    }

    public ConfigurationSection getConfig() {

        ConfigurationSection args = this.config.getConfigurationSection("args");
        if (args == null) args = this.config.createSection("args");
        return args;
    }

    public void accept(T type) {

        accept(type, getConfig());
    }

    public void accept(T type, ConfigurationSection config) {

        action.accept(type, config);
    }
}
