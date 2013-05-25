package de.raidcraft.api.ambient;

import de.raidcraft.util.EnumUtils;
import org.bukkit.Location;

/**
 * @author Silthus
 */
public interface AmbientEffect {

    public enum Type {

        PARTICLE,
        SOUND,
        BUKKIT;

        public static Type fromString(String name) {

            return EnumUtils.getEnumFromString(AmbientEffect.Type.class, name);
        }
    }

    public enum Shape {

        POINT,
        CIRCLE;

        public static Shape fromString(String name) {

            return EnumUtils.getEnumFromString(AmbientEffect.Shape.class, name);
        }
    }

    public void run(Location location);
}
