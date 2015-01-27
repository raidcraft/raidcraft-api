package de.raidcraft.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.UUID;

/**
 * @author Philip Urban, IDragonfire
 */
@Getter
@EqualsAndHashCode
public class ChunkLocation {

    private int x;
    private int z;
    private UUID world;

    public ChunkLocation(int x, int z, UUID world) {
        this.x = x;
        this.z = z;
        this.world = world;
    }

    public ChunkLocation(Chunk chunk) {
        this(chunk.getX(), chunk.getZ(), chunk.getWorld().getUID());
    }

    public ChunkLocation(Location location) {
        this(location.getChunk());
    }


    public Chunk getChunk() {
        return Bukkit.getWorld(getWorld()).getChunkAt(getX(), getZ());
    }
}
