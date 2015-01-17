package de.raidcraft.api.ambient;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public final class AmbientManager {

    public static AmbientEffect getEffect(ConfigurationSection config) throws UnknownAmbientEffect {

        AmbientEffect.Type type = AmbientEffect.Type.fromString(config.getString("type"));
        if (type != null) {
            switch (type) {
                case PARTICLE:
                    return new AmbientParticleEffect(config);
                case BUKKIT:
                    return new AmbientBukkitEffect(config);
                case SOUND:
                    return new AmbientSoundEffect(config);
                case FIREWORK:
                    return new AmbientFireworkEffect(config);
            }
        }
        throw new UnknownAmbientEffect("There is no ambient effect of the type " + config.getString("type"));
    }
}
