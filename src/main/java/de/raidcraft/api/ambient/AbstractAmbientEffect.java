package de.raidcraft.api.ambient;

import de.raidcraft.util.EffectUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

/**
 * @author Silthus
 */
public abstract class AbstractAmbientEffect implements AmbientEffect {

    private final Shape shape;
    // values for the circle shape
    private final int radius;
    private final int height;
    private final int hOffset;
    private final boolean hollow;
    private final boolean sphere;

    protected AbstractAmbientEffect(ConfigurationSection config) {

        this.shape = Shape.fromString(config.getString("shape", "POINT"));
        // if circle shape
        this.radius = config.getInt("radius", 3);
        this.height = config.getInt("height", 1);
        this.hOffset = config.getInt("h-offset", 1);
        this.hollow = config.getBoolean("hollow", true);
        this.sphere = config.getBoolean("sphere", false);
    }

    @Override
    public final void run(Location location) {

        switch (shape) {

            case CIRCLE:
                List<Location> circle = EffectUtil.circle(location, radius, height, hollow, sphere, hOffset);
                runEffect(circle.toArray(new Location[circle.size()]));
                break;
            case POINT:
                runEffect(location);
                break;
        }
    }

    protected abstract void runEffect(Location... locations);
}
