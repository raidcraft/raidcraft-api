package de.raidcraft.api.action.trigger;

import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.TriggerFactory;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class ConfiguredTriggerGroup extends TriggerGroup {

    private final ConfigurationSection config;

    public ConfiguredTriggerGroup(String identifier, ConfigurationSection config) {
        super(identifier);
        this.config = config;
        setDescription(config.getString("desc", ""));
        setEnabled(config.getBoolean("enabled", true));
        setOrdered(config.getBoolean("ordered", false));
        setRequired(config.getInt("required", 0));
    }

    @Override
    protected List<TriggerFactory> loadTrigger() {
        return ActionAPI.createTrigger(config.getConfigurationSection("trigger"));
    }
}
