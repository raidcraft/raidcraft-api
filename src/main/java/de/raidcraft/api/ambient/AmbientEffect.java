package de.raidcraft.api.ambient;

import de.raidcraft.util.EnumUtils;
import org.bukkit.Location;

/**
 * @author Silthus
 */
public interface AmbientEffect {

    enum Type {

        PARTICLE,
        SOUND,
        BUKKIT,
        FIREWORK,
        CUSTOM;

        public static Type fromString(String name) {

            return EnumUtils.getEnumFromString(AmbientEffect.Type.class, name);
        }
    }

    enum Shape {

        POINT,
        CIRCLE;

        public static Shape fromString(String name) {

            return EnumUtils.getEnumFromString(AmbientEffect.Shape.class, name);
        }
    }

    void run(Location location);
}
