package de.raidcraft.api.projectiles.projectile;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

/**
 * Custom projectile interface.
 */
public interface CustomProjectile {

    /**
     * Gets the entity type.
     *
     * @return entity type of projectile
     */
    public EntityType getEntityType();

    /**
     * Gets the entity.
     *
     * @return the entity
     */
    public Entity getEntity();

    /**
     * Gets the shooter.
     *
     * @return the shooter
     */
    public LivingEntity getShooter();

    /**
     * Gets the projectile name.
     *
     * @return the projectile name
     */
    public String getProjectileName();

    /**
     * Sets entity invulnerable.
     *
     * @param value invulnerable state
     */
    public void setInvulnerable(boolean value);

    /**
     * Checks if entity is invulnerable.
     *
     * @return true, if entity is invulnerable
     */
    public boolean isInvulnerable();

}
