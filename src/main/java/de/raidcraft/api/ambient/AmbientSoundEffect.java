package de.raidcraft.api.ambient;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class AmbientSoundEffect extends AbstractAmbientEffect {

    private final Sound sound;
    private final float volume;
    private final float pitch;

    protected AmbientSoundEffect(ConfigurationSection config) {

        super(config);
        this.sound = Sound.valueOf(config.getString("effect"));
        this.volume = (float) config.getDouble("volume", 1.0);
        this.pitch = (float) config.getDouble("pitch", 1.0);
    }

    @Override
    protected void runEffect(Location... locations) {

        for (Location location : locations) {
            location.getWorld().playSound(location, sound, volume, pitch);
        }
    }
}
