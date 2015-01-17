package de.raidcraft.util.data;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * @author Philip Urban
 */
public class ChunkLocation {

    private int x;
    private int z;

    public ChunkLocation(int x, int z) {

        this.x = x;
        this.z = z;
    }

    public ChunkLocation(Chunk chunk) {

        this.x = chunk.getX();
        this.z = chunk.getZ();
    }

    public ChunkLocation(Location location) {

        double cxd = location.getX() / 16D;
        if (cxd < 0) {
            x = (int) Math.ceil(cxd);
        } else {
            x = (int) cxd;
        }

        double czd = location.getZ() / 16D;
        if (czd < 0) {
            z = (int) Math.ceil(czd);
        } else {
            z = (int) czd;
        }
    }

    public int getX() {

        return x;
    }

    public int getZ() {

        return z;
    }

    public Chunk getChunk(World world) {

        return world.getChunkAt(x, z);
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder().append(x).append(z).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof ChunkLocation) {
            ChunkLocation cl = (ChunkLocation) obj;
            return (cl.getX() == x && cl.getZ() == z);
        }
        return false;
    }
}
