package de.raidcraft.api.action.trigger;

import de.raidcraft.RaidCraft;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Silthus
 */
@ToString(of = {"identifier"})
@EqualsAndHashCode(of = {"identifier"})
@Data
public class TriggerFactory {

    private final TriggerManager manager;
    private final String identifier;
    private final ConfigurationSection config;
    private final Set<TriggerListener> registeredListeners = new HashSet<>();

    protected TriggerFactory(TriggerManager manager, String identifier, ConfigurationSection config) {

        this.manager = manager;
        this.identifier = identifier;
        this.config = config;
    }

    public void registerListener(TriggerListener listener) {

        RaidCraft.LOGGER.info("trying to register trigger " + identifier + " for " + listener + "...");
        if (registeredListeners.contains(listener)) return;
        getManager().registerListener(listener, getIdentifier(), getConfig());
        registeredListeners.add(listener);
        RaidCraft.LOGGER.info("...success");
    }

    public void unregisterListener(TriggerListener listener) {

        registeredListeners.remove(listener);
        getManager().unregisterListener(listener);
    }
}
