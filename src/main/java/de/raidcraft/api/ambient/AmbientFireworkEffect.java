package de.raidcraft.api.ambient;

import de.raidcraft.RaidCraft;
import de.raidcraft.util.EffectUtil;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class AmbientFireworkEffect extends AbstractAmbientEffect {

    private final FireworkEffect fireworkEffect;

    protected AmbientFireworkEffect(ConfigurationSection config) {

        super(config);
        FireworkEffect.Builder builder = FireworkEffect.builder();
        builder.with(org.bukkit.FireworkEffect.Type.valueOf(config.getString("effect", "ball")));
        builder.flicker(config.getBoolean("flicker", false));
        builder.trail(config.getBoolean("trail", false));
        for (String color : config.getStringList("colors")) {
            try {
                builder.withColor(Color.fromBGR(Integer.parseInt(color)));
            } catch (IllegalArgumentException e) {
                RaidCraft.LOGGER.warning("Wrong Color format " + color + " in " + config.getRoot().getName());
            }
        }
        for (String color : config.getStringList("fade-colors")) {
            try {
                builder.withFade(Color.fromBGR(Integer.parseInt(color)));
            } catch (IllegalArgumentException e) {
                RaidCraft.LOGGER.warning("Wrong Color format " + color + " in " + config.getRoot().getName());
            }
        }
        this.fireworkEffect = builder.build();
    }

    @Override
    protected void runEffect(Location... locations) {

        for (Location location : locations) {
            EffectUtil.playFirework(location.getWorld(), location, fireworkEffect);
        }
    }
}
