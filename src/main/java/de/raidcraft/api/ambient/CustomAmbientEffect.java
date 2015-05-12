package de.raidcraft.api.ambient;

import de.slikey.effectlib.EffectManager;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

/**
 * @author mdoering
 */
public class CustomAmbientEffect implements AmbientEffect {

    private final EffectManager effectManager;
    private final ConfigurationSection arguments;

    protected CustomAmbientEffect(EffectManager effectManager, ConfigurationSection config) {

        this.effectManager = effectManager;
        this.arguments = config.isConfigurationSection("args") ? config.getConfigurationSection("args") : new MemoryConfiguration();
    }

    @Override
    public void run(Location location) {

        effectManager.start(arguments.getString("class"), arguments, location, null, null, null, null);
    }

    @Override
    public void run(Location from, Location to) {

        effectManager.start(arguments.getString("class"), arguments, from, to, null, null, null);
    }
}
