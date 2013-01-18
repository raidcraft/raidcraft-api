package de.raidcraft.util;

import com.sk89q.worldedit.blocks.BlockType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Silthus
 */
public final class BlockUtil {

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
     * @param source block
     * @param material to replace
     * @param width the x-axis radius
     * @param length the z-axis radius
     * @param height the y-axis radius
     */
    public static Set<Block> replaceNonSolidSurfaceBlocks(Block source, Material material, int width, int length, int height) {

        return replaceNonSolidSurfaceBlocks(source, material, width, length, height, false);
    }

    public static Set<Block> replaceNonSolidSurfaceBlock(Block block, Material material) {

        return replaceNonSolidSurfaceBlock(block, material, false);
    }

    public static Set<Block> replaceNonSolidSurfaceBlock(Block block, Material material, boolean replaceAir) {

        Set<Block> changedBlocks = new HashSet<>();
        if (block.getRelative(0, -1, 0).getTypeId() == 0 || block.getRelative(0, 1, 0).getTypeId() != 0) {
            return changedBlocks;
        }
        if (replaceAir && block.getType() == Material.AIR) {
            block.setType(material);
            changedBlocks.add(block);
        } else if (block.isLiquid() || !BlockType.canPassThrough(block.getTypeId())) {
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
}
