package de.raidcraft.api.ambient;

import de.raidcraft.RaidCraft;
import de.raidcraft.util.ConfigUtil;
import de.raidcraft.util.EnumUtils;
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
        this.sound = EnumUtils.getEnumFromString(Sound.class, config.getString("effect"));
        this.volume = (float) config.getDouble("volume", 1.0);
        this.pitch = (float) config.getDouble("pitch", 1.0);
        if (sound == null) {
            RaidCraft.LOGGER.warning("Invalid sound effect type: " + config.getString("effect") + " in " + ConfigUtil.getFileName(config));
        }
    }

    @Override
    protected void runEffect(Location... locations) {

        if (sound == null) {
            return;
        }
        for (Location location : locations) {
            location.getWorld().playSound(location, sound, volume, pitch);
        }
    }

    @Override
    public String toString() {

        return super.toString() + "{AmbientSoundEffect{" +
                "sound=" + sound +
                ", volume=" + volume +
                ", pitch=" + pitch +
                "}}";
    }
}
