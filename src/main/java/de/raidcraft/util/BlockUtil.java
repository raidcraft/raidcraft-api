package de.raidcraft.util;

import com.sk89q.worldedit.blocks.BlockType;
import org.bukkit.Material;
import org.bukkit.block.Block;

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
    public static void replaceNonSolidSurfaceBlocks(Block source, Material material, int width, int length, int height) {

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    replaceNonSolidSurfaceBlock(source.getRelative(x, y, z), material);
                    replaceNonSolidSurfaceBlock(source.getRelative(-x, -y, -z), material);
                }
            }
        }
    }

    public static void replaceNonSolidSurfaceBlock(Block block, Material material) {

        if (block.getRelative(0, -1, 0).getTypeId() == 0 || block.getRelative(0, 1, 0).getTypeId() != 0) {
            return;
        }
        if (block.isLiquid() || !BlockType.canPassThrough(block.getTypeId())) {
            replaceNonSolidSurfaceBlock(block.getRelative(0, 1, 0), material);
        } else {
            block.setType(material);
        }
    }
}
