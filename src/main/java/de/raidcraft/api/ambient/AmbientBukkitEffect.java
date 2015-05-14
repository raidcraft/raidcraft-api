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
    private final float speed;
    private final int particleCount;

    protected AmbientBukkitEffect(ConfigurationSection config) {

        super(config);
        this.effectType = Effect.valueOf(config.getString("effect"));
        this.data = config.getInt("data", 1);
        this.radius = config.getInt("radius", 100);
        this.speed = (float) config.getDouble("speed", 1.0);
        this.particleCount = config.getInt("particle-count", 10);
    }

    @Override
    protected void runEffect(Location... locations) {

        for (Location location : locations) {
            location.getWorld().playEffect(location, effectType, data, radius);
        }
    }

    @Override
    public String toString() {

        return super.toString() + "{AmbientBukkitEffect{" +
                "effectType=" + effectType +
                ", data=" + data +
                ", radius=" + radius +
                "}}";
    }
}
