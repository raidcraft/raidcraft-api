package de.raidcraft.api.ambient;

import com.comphenix.protocol.wrappers.EnumWrappers;
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
        this.particleEffect = EnumWrappers.Particle.valueOf(config.getString("effect").toUpperCase());
        this.amount = config.getInt("amount", 1);
        this.xOffset = (float) config.getDouble("x-offset", 0.25);
        this.yOffset = (float) config.getDouble("y-offset", 0.25);
        this.zOffset = (float) config.getDouble("z-offset", 0.25);
    }

    @Override
    public void runEffect(Location... locations) {

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
