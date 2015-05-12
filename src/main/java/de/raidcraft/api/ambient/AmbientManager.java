package de.raidcraft.api.ambient;

import de.slikey.effectlib.EffectLib;
import de.slikey.effectlib.EffectManager;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public final class AmbientManager {

    private static EffectLib effectLib;
    private static EffectManager effectManager;

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
                case CUSTOM:
                    return new CustomAmbientEffect(getEffectManager(), config);
            }
        }
        throw new UnknownAmbientEffect("There is no ambient effect of the type " + config.getString("type"));
    }

    public static EffectManager getEffectManager() {

        if (effectManager == null) {
            effectLib = new EffectLib();
            effectLib.onEnable();
            effectManager = new EffectManager(effectLib);
        }
        return effectManager;
    }
}
