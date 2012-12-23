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

    public static <T extends LivingEntity> T getTarget(final LivingEntity source, final Iterable<T> entities) {

        T target = null;
        double targetDistanceSquared = 0;
        final double radiusSquared = 1;
        final Vector l = source.getEyeLocation().toVector(), n = source.getLocation().getDirection().normalize();
        final double cos45 = Math.cos(Math.PI / 4);
        for (final T other : entities) {
            if (other == source)
                continue;
            if (target == null || targetDistanceSquared > other.getLocation().distanceSquared(source.getLocation())) {
                final Vector t = other.getLocation().add(0, 1, 0).toVector().subtract(l);
                if (n.clone().crossProduct(t).lengthSquared() < radiusSquared && t.normalize().dot(n) >= cos45) {
                    target = other;
                    targetDistanceSquared = target.getLocation().distanceSquared(source.getLocation());
                }
            }
        }
        return target;
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
