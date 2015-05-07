package de.raidcraft.util;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Silthus, Me4502
 */
public final class LocationUtil {

    private LocationUtil() {

    }

    public static boolean isWithinRadius(Location l1, Location l2, int radius) {

        if (l1.getWorld() != null && l2.getWorld() != null) {
            return l1.getWorld().equals(l2.getWorld()) && getDistanceSquared(l1,
                    l2) <= radius * radius;
        }
        return false;
    }

    public static Entity[] getNearbyEntities(Location l, int radius) {

        int chunkRadius = radius < 16 ? 1 : (radius - radius % 16) / 16;
        HashSet<Entity> radiusEntities = new HashSet<>();
        for (int chX = 0 - chunkRadius; chX <= chunkRadius; chX++) {
            for (int chZ = 0 - chunkRadius; chZ <= chunkRadius; chZ++) {
                int x = (int) l.getX(), y = (int) l.getY(), z = (int) l.getZ();
                for (Entity e : new Location(l.getWorld(), x + chX * 16, y, z + chZ * 16).getChunk().getEntities()) {
                    if (e.getLocation().getWorld().equals(l.getWorld())
                            && e.getLocation().distanceSquared(l) <= radius * radius && e.getLocation().getBlock() != l.getBlock()) {
                        radiusEntities.add(e);
                    }
                }
            }
        }
        return radiusEntities.toArray(new Entity[radiusEntities.size()]);
    }

    /**
     * Gets the distance between two points.
     *
     * @param l1
     * @param l2
     *
     * @return
     */
    public static double getDistance(Location l1, Location l2) {

        return getBlockDistance(l1, l2);
    }

    public static double getDistanceSquared(Location l1, Location l2) {

        return getBlockDistance(l1, l2) * getBlockDistance(l1, l2);
    }

    /**
     * Gets the greatest distance between two locations. Only takes
     * int locations and does not check a round radius.
     *
     * @param l1 to compare
     * @param l2 to compare
     *
     * @return greatest distance
     */
    public static int getBlockDistance(Location l1, Location l2) {

        int x = Math.abs(l1.getBlockX() - l2.getBlockX());
        int y = Math.abs(l1.getBlockY() - l2.getBlockY());
        int z = Math.abs(l1.getBlockZ() - l2.getBlockZ());
        if (x >= y && x >= z) {
            return x;
        } else if (y >= x && y >= z) {
            return y;
        } else if (z >= x && z >= y) {
            return z;
        } else {
            return x;
        }
    }

    /**
     * @return the correct distance between blocks without y layer
     */
    public static double getRealDistance(double x1, double z1, double x2, double z2) {

        double dx = Math.abs(x2 - x1);
        double dy = Math.abs(z2 - z1);
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Gets the block located relative to the signs facing. That
     * means that when the sign is attached to a block and the player
     * is looking at it it will add the offsetX to left or right,
     * offsetY is added up or down and offsetZ is added front or back.
     *
     * @param block   to get relative position from
     * @param facing  to work with
     * @param offsetX amount to move left(negative) or right(positive)
     * @param offsetY amount to move up(positive) or down(negative)
     * @param offsetZ amount to move back(negative) or front(positive)
     *
     * @return block located at the relative offset position
     */
    public static Block getRelativeOffset(Block block, BlockFace facing, int offsetX, int offsetY, int offsetZ) {

        BlockFace front = facing;
        BlockFace back;
        BlockFace right;
        BlockFace left;

        switch (front) {

            case SOUTH:
                back = BlockFace.NORTH;
                left = BlockFace.EAST;
                right = BlockFace.WEST;
                break;
            case WEST:
                back = BlockFace.EAST;
                left = BlockFace.SOUTH;
                right = BlockFace.NORTH;
                break;
            case NORTH:
                back = BlockFace.SOUTH;
                left = BlockFace.WEST;
                right = BlockFace.EAST;
                break;
            case EAST:
                back = BlockFace.WEST;
                left = BlockFace.NORTH;
                right = BlockFace.SOUTH;
                break;
            default:
                back = BlockFace.SOUTH;
                left = BlockFace.EAST;
                right = BlockFace.WEST;
        }

        // apply left and right offset
        if (offsetX > 0) {
            block = getRelativeBlock(block, right, offsetX);
        } else if (offsetX < 0) {
            block = getRelativeBlock(block, left, offsetX);
        }

        // apply front and back offset
        if (offsetZ > 0) {
            block = getRelativeBlock(block, front, offsetZ);
        } else if (offsetZ < 0) {
            block = getRelativeBlock(block, back, offsetZ);
        }

        // apply up and down offset
        if (offsetY > 0) {
            block = getRelativeBlock(block, BlockFace.UP, offsetY);
        } else if (offsetY < 0) {
            block = getRelativeBlock(block, BlockFace.DOWN, offsetY);
        }
        return block;
    }

    /**
     * Gets all surrounding chunks near the given block and radius.
     *
     * @param block  to get surrounding chunks for
     * @param radius around the block
     *
     * @return chunks in the given radius
     */
    public static Set<Chunk> getSurroundingChunks(Block block, int radius) {

        Chunk chunk = block.getChunk();
        radius = radius / 16 + 1;
        Set<Chunk> chunks = new LinkedHashSet<>();
        World world = chunk.getWorld();
        int cX = chunk.getX();
        int cZ = chunk.getZ();
        for (int x = radius; x >= 0; x--) {
            for (int z = radius; z >= 0; z--) {
                chunks.add(world.getChunkAt(cX + x, cZ + z));
                chunks.add(world.getChunkAt(cX - x, cZ - z));
                chunks.add(world.getChunkAt(cX + x, cZ - z));
                chunks.add(world.getChunkAt(cX - x, cZ + z));
                chunks.add(world.getChunkAt(cX + x, cZ));
                chunks.add(world.getChunkAt(cX - x, cZ));
                chunks.add(world.getChunkAt(cX, cZ + z));
                chunks.add(world.getChunkAt(cX, cZ - z));
            }
        }
        return chunks;
    }

    /**
     * Get relative block X that way.
     *
     * @param block
     * @param facing
     * @param amount
     *
     * @return The block
     */
    private static Block getRelativeBlock(Block block, BlockFace facing, int amount) {

        amount = Math.abs(amount);
        for (int i = 0; i < amount; i++) {
            block = block.getRelative(facing);
        }
        return block;
    }

    /**
     * Gets next vertical free space
     *
     * @param block
     * @param direction
     *
     * @return next air block vertically.
     */
    public static Block getNextFreeSpace(Block block, BlockFace direction) {

        while (block.getType() != Material.AIR && block.getRelative(direction).getType() != Material.AIR) {
            if (!(block.getY() < block.getWorld().getMaxHeight())) {
                break;
            }
            block = block.getRelative(direction);
        }
        return block;
    }

    /**
     * Gets centre of passed block.
     *
     * @param block
     *
     * @return Centre location
     */
    public static Location getCenterOfBlock(Block block) {

        Location location = block.getLocation();
        location.setX(block.getX() + 0.5);
        location.setZ(block.getZ() + 0.5);
        location.setY(block.getY() + 1);
        return location;
    }

    public static BlockFace flipBlockFace(BlockFace face) {

        switch (face) {

            case DOWN:
                return BlockFace.UP;
            case UP:
                return BlockFace.DOWN;
            case NORTH:
                return BlockFace.SOUTH;
            case SOUTH:
                return BlockFace.NORTH;
            case WEST:
                return BlockFace.EAST;
            case EAST:
                return BlockFace.WEST;
            // TODO: add more face flipping
            default:
                return face;
        }
    }

    public static BlockFace rotateBlockFace(BlockFace face) {

        switch (face) {

            case WEST:
                return BlockFace.NORTH;
            case NORTH:
                return BlockFace.EAST;
            case EAST:
                return BlockFace.SOUTH;
            case SOUTH:
                return BlockFace.WEST;
            default:
                return face;
        }
    }

    public static List<Player> getNearbyPlayers(Block block, int radius) {

        List<Player> players = new ArrayList<>();
        for (Chunk chunk : getSurroundingChunks(block, radius)) {
            for (Entity e : chunk.getEntities()) {
                if (e instanceof Player) {
                    players.add((Player) e);
                }
            }
        }
        return players;
    }

    public static boolean isSafeZone(Player player, Location location) {

        return !isMobDamageAllowed(player, location) || !isPvPAllowed(player, location);
    }

    public static boolean isMobDamageAllowed(Player player, Location location) {

        Optional<WorldGuardPlugin> worldGuard = RaidCraft.getComponent(RaidCraftPlugin.class).getWorldGuard();
        if (worldGuard.isPresent()) {
            ApplicableRegionSet regions = worldGuard.get().getRegionManager(location.getWorld()).getApplicableRegions(location);
            return regions.queryState(worldGuard.get().wrapPlayer(player), DefaultFlag.MOB_DAMAGE) == StateFlag.State.ALLOW;
        }
        return true;
    }

    public static boolean isPvPAllowed(Player player, Location location) {

        Optional<WorldGuardPlugin> worldGuard = RaidCraft.getComponent(RaidCraftPlugin.class).getWorldGuard();
        if (worldGuard.isPresent()) {
            ApplicableRegionSet regions = worldGuard.get().getRegionManager(location.getWorld()).getApplicableRegions(location);
            return regions.queryState(worldGuard.get().wrapPlayer(player), DefaultFlag.PVP) == StateFlag.State.ALLOW;
        }
        return true;
    }

    public static Vector getRevertedViewDirection(Location location) {

        double pitch = ((90 - location.getPitch()) * Math.PI) / 180;
        double yaw = ((location.getYaw() + 90 + 180) * Math.PI) / 180;

        double x = Math.sin(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch) * Math.sin(yaw);
        double z = Math.cos(pitch);

        return new Vector(x, z, y);
    }

    public static Vector getDirection(Location from, Location to) {

        return to.toVector().subtract(from.toVector());
    }
}
