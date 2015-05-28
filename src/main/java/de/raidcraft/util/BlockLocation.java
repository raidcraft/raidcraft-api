package de.raidcraft.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.UUID;

/**
 * @author Philip Urban, IDragonfire
 */
@Getter
@EqualsAndHashCode
public class BlockLocation {

    private int x;
    private int y;
    private int z;
    private UUID world;

    public BlockLocation(int x, int y, int z, UUID world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    public BlockLocation(Block block) {
        this(block.getX(), block.getY(), block.getZ(),block.getWorld().getUID());
    }

    public BlockLocation(Location location) {
        this(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getUID());
    }

    public Block getChunk() {
        return Bukkit.getWorld(getWorld()).getBlockAt(getX(), getY(), getZ());
    }
}
