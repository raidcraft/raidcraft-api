package de.raidcraft.api.action.trigger;

import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
@Data
public class TriggerFactory {

    private final TriggerManager manager;
    private final String identifier;
    private final ConfigurationSection config;

    protected TriggerFactory(TriggerManager manager, String identifier, ConfigurationSection config) {

        this.manager = manager;
        this.identifier = identifier;
        this.config = config;
    }

    public void registerListener(TriggerListener listener) {

        getManager().registerListener(listener, getIdentifier(), getConfig());
    }

    public void unregisterListener(TriggerListener listener) {

        getManager().unregisterListener(listener);
    }
}
