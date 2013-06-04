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

    public static final HashSet<Byte> TRANSPARENT_BLOCKS = new HashSet<>();

    static {

        TRANSPARENT_BLOCKS.add((byte) 0);
        TRANSPARENT_BLOCKS.add((byte) 8);
        TRANSPARENT_BLOCKS.add((byte) 9);
        TRANSPARENT_BLOCKS.add((byte) 10);
        TRANSPARENT_BLOCKS.add((byte) 11);
        TRANSPARENT_BLOCKS.add((byte) 30);
        TRANSPARENT_BLOCKS.add((byte) 31);
        TRANSPARENT_BLOCKS.add((byte) 32);
        TRANSPARENT_BLOCKS.add((byte) 37);
        TRANSPARENT_BLOCKS.add((byte) 38);
        TRANSPARENT_BLOCKS.add((byte) 39);
        TRANSPARENT_BLOCKS.add((byte) 40);
        TRANSPARENT_BLOCKS.add((byte) 51);
        TRANSPARENT_BLOCKS.add((byte) 55);
        TRANSPARENT_BLOCKS.add((byte) 59);
        TRANSPARENT_BLOCKS.add((byte) 70);
        TRANSPARENT_BLOCKS.add((byte) 72);
        TRANSPARENT_BLOCKS.add((byte) 75);
        TRANSPARENT_BLOCKS.add((byte) 83);
        TRANSPARENT_BLOCKS.add((byte) 90);
        TRANSPARENT_BLOCKS.add((byte) 93);
        TRANSPARENT_BLOCKS.add((byte) 94);
        TRANSPARENT_BLOCKS.add((byte) 106);
        TRANSPARENT_BLOCKS.add((byte) 147);
        TRANSPARENT_BLOCKS.add((byte) 148);
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
}
