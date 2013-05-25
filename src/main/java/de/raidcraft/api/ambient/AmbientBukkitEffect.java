package de.raidcraft.api.ambient;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class AmbientBukkitEffect extends AbstractAmbientEffect {

    private final Effect effectType;
    private final int data;
    private final int radius;

    protected AmbientBukkitEffect(ConfigurationSection config) {

        super(config);
        this.effectType = Effect.valueOf(config.getString("effect"));
        this.data = config.getInt("data", 1);
        this.radius = config.getInt("radius", 100);
    }

    @Override
    protected void runEffect(Location... locations) {

        for (Location location : locations) {
            location.getWorld().playEffect(location, effectType, data, radius);
        }
    }
}
