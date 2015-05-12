package de.raidcraft.api.ambient;

import de.slikey.effectlib.EffectManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.LivingEntity;

/**
 * @author mdoering
 */
public class CustomAmbientEffect implements AmbientEffect {

    private final EffectManager effectManager;
    private final ConfigurationSection arguments;
    @Getter
    @Setter
    private LivingEntity entity;
    @Getter
    @Setter
    private LivingEntity target;
    @Getter
    @Setter
    private Location locationTarget;

    protected CustomAmbientEffect(EffectManager effectManager, ConfigurationSection config) {

        this.effectManager = effectManager;
        this.arguments = config.isConfigurationSection("args") ? config.getConfigurationSection("args") : new MemoryConfiguration();
    }

    @Override
    public void run(Location location) {

        effectManager.start(arguments.getString("class"), arguments, location, getLocationTarget(), getEntity(), getTarget(), null);
    }
}
