package de.raidcraft.api.locations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import io.ebean.annotation.NotNull;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.io.InvalidObjectException;
import java.util.Optional;

/**
 * A {@link ConfiguredLocation} is a wrapper around the standard {@link Location}
 * with an additional {@link ConfiguredLocation#getRadius()}.
 *
 * Use the static methods to parse various inputs into a {@link ConfiguredLocation}.
 */
@Data
public class ConfiguredLocation implements Cloneable {

    private final Location location;
    private final int radius;

    protected ConfiguredLocation(Location location) {
        this.location = location;
        this.radius = 0;
    }

    protected ConfiguredLocation(Location location, int radius) {
        this.location = location;
        this.radius = radius;
    }

    protected ConfiguredLocation(ConfigurationSection config) {
        this.location = new Location(
                Bukkit.getWorld(config.getString("world", "world")),
                config.getDouble("x"),
                config.getDouble("y"),
                config.getDouble("z"),
                config.getLong("yaw", 0),
                config.getLong("pitch", 0)
            );
        this.radius = config.getInt("radius", 0);
    }

    @Override
    protected ConfiguredLocation clone() {
        return new ConfiguredLocation(getLocation().clone(), getRadius());
    }
}
