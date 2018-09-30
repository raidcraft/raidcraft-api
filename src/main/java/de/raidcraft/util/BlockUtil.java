package de.raidcraft.util;

import com.sk89q.worldedit.blocks.BlockType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author Silthus
 */
public final class BlockUtil {

    public static final HashSet<Material> TRANSPARENT_BLOCKS = new HashSet<>();

    static {

        TRANSPARENT_BLOCKS.add(Material.AIR);
        TRANSPARENT_BLOCKS.add(Material.SAPLING);
        TRANSPARENT_BLOCKS.add(Material.WATER);
        TRANSPARENT_BLOCKS.add(Material.STATIONARY_WATER);
        TRANSPARENT_BLOCKS.add(Material.LAVA);
        TRANSPARENT_BLOCKS.add(Material.STATIONARY_LAVA);
        TRANSPARENT_BLOCKS.add(Material.WEB);
        TRANSPARENT_BLOCKS.add(Material.GRASS);
        TRANSPARENT_BLOCKS.add(Material.LONG_GRASS);
        TRANSPARENT_BLOCKS.add(Material.DEAD_BUSH);
        TRANSPARENT_BLOCKS.add(Material.YELLOW_FLOWER);
        TRANSPARENT_BLOCKS.add(Material.RED_ROSE);
        TRANSPARENT_BLOCKS.add(Material.CROPS);
        TRANSPARENT_BLOCKS.add(Material.SUGAR_CANE_BLOCK);
        TRANSPARENT_BLOCKS.add(Material.SUGAR_CANE);
        TRANSPARENT_BLOCKS.add(Material.BROWN_MUSHROOM);
        TRANSPARENT_BLOCKS.add(Material.RED_MUSHROOM);
        TRANSPARENT_BLOCKS.add(Material.FIRE);
        TRANSPARENT_BLOCKS.add(Material.RAILS);
        TRANSPARENT_BLOCKS.add(Material.LADDER);
        TRANSPARENT_BLOCKS.add(Material.SNOW);
        TRANSPARENT_BLOCKS.add(Material.ACTIVATOR_RAIL);
        TRANSPARENT_BLOCKS.add(Material.SIGN_POST);
        TRANSPARENT_BLOCKS.add(Material.VINE);
        TRANSPARENT_BLOCKS.add(Material.PUMPKIN_STEM);
        TRANSPARENT_BLOCKS.add(Material.CARPET);
    }

    public static Set<Block> replaceNonSolidSurfaceBlocks(Block source, Material material, int width, int length, int height, boolean replaceAir) {

        Set<Block> changedBlocks = new HashSet<>();
        for (int x = 0; x <= width; x++) {
            for (int y = 0; y <= height; y++) {
                for (int z = 0; z <= length; z++) {
                    changedBlocks.addAll(replaceNonSolidSurfaceBlock(source.getRelative(x, y, z), material, replaceAir));
                    changedBlocks.addAll(replaceNonSolidSurfaceBlock(source.getRelative(-x, -y, -z), material, replaceAir));
                }
            }
        }
        return changedBlocks;
    }

    /**
     * Replaces all blocks around the given block that are non solid or air.
     *
     * @param source   block
     * @param material to replace
     * @param width    the x-axis radius
     * @param length   the z-axis radius
     * @param height   the y-axis radius
     */
    public static Set<Block> replaceNonSolidSurfaceBlocks(Block source, Material material, int width, int length, int height) {

        return replaceNonSolidSurfaceBlocks(source, material, width, length, height, false);
    }

    public static Set<Block> replaceNonSolidSurfaceBlock(Block block, Material material) {

        return replaceNonSolidSurfaceBlock(block, material, false);
    }

    public static Set<Block> replaceNonSolidSurfaceBlock(Block block, Material material, boolean replaceAir) {

        Set<Block> changedBlocks = new HashSet<>();
        if (replaceAir && block.getTypeId() == 0) {
            block.setType(material);
            changedBlocks.add(block);
            return changedBlocks;
        }
        if (block.getRelative(0, -1, 0).getTypeId() == 0 || block.getRelative(0, 1, 0).getTypeId() != 0) {
            return changedBlocks;
        }
        if (block.isLiquid() || !BlockType.canPassThrough(block.getTypeId())) {
            changedBlocks.addAll(replaceNonSolidSurfaceBlock(block.getRelative(0, 1, 0), material, replaceAir));
        } else {
            block.setType(material);
            changedBlocks.add(block);
        }
        return changedBlocks;
    }

    public static Set<Block> replaceNonSolidSurfaceBlocks(Block source, Material material, BlockFace direction, int radius) {

        return replaceNonSolidSurfaceBlocks(source, material, direction, radius, false);
    }

    public static Set<Block> replaceNonSolidSurfaceBlocks(Block source, Material material, BlockFace direction, int radius, boolean replaceAir) {

        Set<Block> changedBlocks = new HashSet<>();
        switch (direction) {

            case WEST:
            case EAST:
                changedBlocks.addAll(replaceNonSolidSurfaceBlocks(source, material, radius, 0, 0, replaceAir));
                break;
            case NORTH:
            case SOUTH:
                changedBlocks.addAll(replaceNonSolidSurfaceBlocks(source, material, 0, radius, 0, replaceAir));
                break;
            default:
                changedBlocks.addAll(replaceNonSolidSurfaceBlocks(source, material, 0, 0, radius, replaceAir));
                break;
        }
        return changedBlocks;
    }

    @Deprecated
    public static Set<Block> getBlocks(Block source, int radius, Set<Integer> types) {

        Set<Block> blocks = new HashSet<>();
        Block block;
        for (int x = 0; x < radius; x++) {
            for (int y = 0; y < radius; y++) {
                for (int z = 0; z < radius; z++) {
                    block = source.getRelative(x, y, z);
                    if (types.contains(block.getTypeId())) {
                        blocks.add(block);
                    }
                    block = source.getRelative(-x, -y, -z);
                    if (types.contains(block.getTypeId())) {
                        blocks.add(block);
                    }
                }
            }
        }
        return blocks;
    }

    public static Set<Block> getBlocksFlat(Block source, int radius, Set<Material> types) {

        Set<Block> blocks = new HashSet<>();
        int y = 0;
        Block block;
        for (int x = -radius; x < radius; x++) {
            for (int z = -radius; z < radius; z++) {
                block = source.getRelative(x, y, z);
                if (types.contains(block.getType())) {
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }


    public static void destroyBlock(Block block) {
        // TODO: check if allowed?
        block.breakNaturally();
    }

    @Nullable
    public static Block getTargetBlock(Player player) {

        return getTargetBlock(player, block -> block.getType() != Material.AIR);
    }

    @Nullable
    public static Block getTargetBlock(Player player, Predicate<Block> predicate) {

        BlockIterator blockIterator = new BlockIterator(player, 25);
        while (blockIterator.hasNext()) {
            Block block = blockIterator.next();
            if (predicate.test(block)) {
                return block;
            }
        }
        return null;
    }
}
