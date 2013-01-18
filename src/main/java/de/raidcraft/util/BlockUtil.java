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

        Set<Block> changedBlocks = new HashSet<>();
        for (int x = 0; x <= width; x++) {
            for (int y = 0; y <= height; y++) {
                for (int z = 0; z <= length; z++) {
                    changedBlocks.addAll(replaceNonSolidSurfaceBlock(source.getRelative(x, y, z), material));
                    changedBlocks.addAll(replaceNonSolidSurfaceBlock(source.getRelative(-x, -y, -z), material));
                }
            }
        }
        return changedBlocks;
    }

    public static Set<Block> replaceNonSolidSurfaceBlock(Block block, Material material) {

        Set<Block> changedBlocks = new HashSet<>();
        if (block.getRelative(0, -1, 0).getTypeId() == 0 || block.getRelative(0, 1, 0).getTypeId() != 0) {
            return changedBlocks;
        }
        if (block.isLiquid() || !BlockType.canPassThrough(block.getTypeId())) {
            changedBlocks.addAll(replaceNonSolidSurfaceBlock(block.getRelative(0, 1, 0), material));
        } else {
            block.setType(material);
            changedBlocks.add(block);
        }
        return changedBlocks;
    }

    public static Set<Block> replaceNonSolidSurfaceBlocks(Block source, Material material, BlockFace direction, int radius) {

        Set<Block> changedBlocks = new HashSet<>();
        switch (direction) {

            case WEST:
            case EAST:
                changedBlocks.addAll(replaceNonSolidSurfaceBlocks(source, material, radius, 0, 0));
                break;
            case NORTH:
            case SOUTH:
                changedBlocks.addAll(replaceNonSolidSurfaceBlocks(source, material, 0, radius, 0));
                break;
            default:
                changedBlocks.addAll(replaceNonSolidSurfaceBlocks(source, material, 0, 0, radius));
                break;
        }
        return changedBlocks;
    }
}
