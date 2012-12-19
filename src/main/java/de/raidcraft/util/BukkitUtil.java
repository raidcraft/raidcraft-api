package de.raidcraft.util;

import com.sk89q.worldedit.BlockWorldVector;
import com.sk89q.worldedit.WorldVector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public final class BukkitUtil {

    private BukkitUtil() {

    }

    public static final WorldVector SPAWN = toWorldVector(Bukkit.getWorld("world").getSpawnLocation());

    /**
     * Gets the WorldEdit {@link com.sk89q.worldedit.WorldVector} from the Bukkit {@link org.bukkit.Location}
     *
     * @param location of the vector
     *
     * @return {@link com.sk89q.worldedit.WorldVector}
     */
    public static WorldVector toWorldVector(Location location) {

        return new WorldVector(new BukkitWorld(location.getWorld()), location.getX(), location.getY(), location.getZ());
    }

    public static WorldVector getWorldVector(String world, int x, int y, int z) {

        return new WorldVector(new BukkitWorld(Bukkit.getWorld(world)), x, y, z);
    }

    /**
     * Gets the bukkit {@link org.bukkit.Location} from the WorldEdit {@link com.sk89q.worldedit.WorldVector}.
     *
     * @param vector location
     *
     * @return {@link org.bukkit.Location}
     */
    public static Location getLocation(WorldVector vector) {

        return new Location(Bukkit.getWorld(vector.getWorld().getName()), vector.getX(), vector.getY(), vector.getZ());
    }

    public static void callEvent(Event event) {

        Bukkit.getPluginManager().callEvent(event);
    }

    public static Player getTargetPlayer(final Player player) {

        return getTarget(player, player.getWorld().getPlayers());
    }

    public static LivingEntity getTargetEntity(final LivingEntity entity, final Class<? extends LivingEntity> type) {

        if (entity instanceof Creature)
            return ((Creature) entity).getTarget();
        return getTarget(entity, entity.getWorld().getEntitiesByClass(type));
    }

    public static <T extends LivingEntity> T getTarget(final LivingEntity entity, final Iterable<T> entities) {

        if (entity == null)
            return null;
        T target = null;
        double targetDistanceSquared = Double.MAX_VALUE;
        final double radiusSquared = 1;
        final Vector l = entity.getEyeLocation().toVector(),
                n = entity.getLocation().getDirection().normalize();
        final double cos = Math.cos(Math.PI / 4);
        for (final T other : entities) {
            if (other == entity)
                continue;
            if (target == null || targetDistanceSquared > other.getLocation().distanceSquared(entity.getLocation())) {
                final Vector t = other.getLocation().toVector().subtract(l);
                if (n.clone().crossProduct(t).lengthSquared() < radiusSquared && t.normalize().dot(n) >= cos) {
                    target = other;
                    targetDistanceSquared = target.getLocation().distanceSquared(entity.getLocation());
                }
            }
        }
        if (target != null && entity.getLineOfSight(null, 100).contains(target.getWorld().getBlockAt(target.getLocation()))) {
            return target;
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static List<LivingEntity> getNearbyEntities(final LivingEntity source, int radius) {

        List<LivingEntity> entities = new ArrayList<>();
        for (Entity entity : source.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof LivingEntity) {
                entities.add((LivingEntity) entity);
            }
        }
        return entities;
    }

    public static BlockWorldVector toBlockWorldVector(Block block) {

        return new BlockWorldVector(new BukkitWorld(block.getWorld()), toWorldVector(block.getLocation()));
    }
}
