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
    private final double frequency;

    private double executions = 0;

    protected AbstractAmbientEffect(ConfigurationSection config) {

        this.shape = Shape.fromString(config.getString("shape", "POINT"));
        // if circle shape
        this.radius = config.getInt("radius", 3);
        this.height = config.getInt("height", 1);
        this.hOffset = config.getInt("h-offset", 1);
        this.hollow = config.getBoolean("hollow", true);
        this.sphere = config.getBoolean("sphere", false);
        this.frequency = config.getDouble("frequency", 1.0);
    }

    @Override
    public final void run(Location location) {

        executions += frequency;
        int execute = (int) executions;
        // execute the task as soon as the counter is above 1.0
        for (int i = 0; i < execute; i++) {
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
        // substract the executed amount to soft reset the counter
        // this is needed when the frequency is 0.75 for example
        executions -= execute;
    }

    protected abstract void runEffect(Location... locations);
}
