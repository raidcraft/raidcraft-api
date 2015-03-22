package de.raidcraft.api.mobs;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

/**
 * @author Silthus
 */
public interface CustomNmsEntity {

    public UUID getUniqueID();

    public LivingEntity spawn(Location location);
}
