package de.raidcraft.util;

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
        if (replaceAir && block.getType() == Material.AIR) {
            block.setType(material);
            changedBlocks.add(block);
            return changedBlocks;
        }
        if (block.getRelative(0, -1, 0).getType() == Material.AIR || block.getRelative(0, 1, 0).getType() != Material.AIR) {
            return changedBlocks;
        }
        if (block.isLiquid() || !block.getType().isSolid()) {
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
