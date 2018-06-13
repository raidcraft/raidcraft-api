package de.raidcraft.api.ambient;

import com.comphenix.protocol.wrappers.EnumWrappers;
import de.raidcraft.RaidCraft;
import de.raidcraft.tables.RcLogLevel;
import de.raidcraft.util.ConfigUtil;
import de.raidcraft.util.EnumUtils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class AmbientParticleEffect extends AbstractAmbientEffect {

    private final EnumWrappers.Particle particleEffect;
    private final int amount;
    private final float xOffset;
    private final float yOffset;
    private final float zOffset;

    protected AmbientParticleEffect(ConfigurationSection config) {

        super(config);
        this.particleEffect = EnumUtils.getEnumFromString(EnumWrappers.Particle.class, config.getString("effect"));
        this.amount = config.getInt("amount", 1);
        this.xOffset = (float) config.getDouble("x-offset", 0.25);
        this.yOffset = (float) config.getDouble("y-offset", 0.25);
        this.zOffset = (float) config.getDouble("z-offset", 0.25);
        if (particleEffect == null) {
            RaidCraft.log("Invalid particle effect type: " + config.getString("effect") + " in " + ConfigUtil.getFileName(config),
                    "AmbientParticleEffect", RcLogLevel.WARNING);
        }
    }

    @Override
    public void runEffect(Location... locations) {

        if (particleEffect == null) return;

        for (Location location : locations) {
            ParticleEffect.sendToLocation(particleEffect, location, xOffset, yOffset, zOffset, amount);
        }
    }

    @Override
    public String toString() {

        return super.toString() + "{AmbientParticleEffect{" +
                "particleEffect=" + particleEffect +
                ", amount=" + amount +
                ", xOffset=" + xOffset +
                ", yOffset=" + yOffset +
                ", zOffset=" + zOffset +
                "}}";
    }
}
