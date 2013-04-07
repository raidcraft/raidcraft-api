package de.raidcraft;

import de.raidcraft.api.database.Bean;
import org.bukkit.block.Block;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
* @author Silthus
*/
@Entity
@Table(name = "rc_player_placed_blocks")
public class PlayerPlacedBlock implements Bean {

    @Id
    private int id;
    private String world;
    private int chunkX;
    private int chunkZ;
    private int x;
    private int y;
    private int z;
    private Timestamp timestamp;

    public PlayerPlacedBlock() {

    }

    public PlayerPlacedBlock(Block block) {

        this(block.getWorld().getName(), block.getChunk().getX(), block.getChunk().getZ(), block.getX(), block.getY(), block.getZ());
    }

    public PlayerPlacedBlock(String world, int chunkX, int chunkZ, int x, int y, int z) {

        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.x = x;
        this.y = y;
        this.z = z;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getWorld() {

        return world;
    }

    public void setWorld(String world) {

        this.world = world;
    }

    public int getChunkX() {

        return chunkX;
    }

    public void setChunkX(int chunkX) {

        this.chunkX = chunkX;
    }

    public int getChunkZ() {

        return chunkZ;
    }

    public void setChunkZ(int chunkZ) {

        this.chunkZ = chunkZ;
    }

    public int getX() {

        return x;
    }

    public void setX(int x) {

        this.x = x;
    }

    public int getY() {

        return y;
    }

    public void setY(int y) {

        this.y = y;
    }

    public int getZ() {

        return z;
    }

    public void setZ(int z) {

        this.z = z;
    }

    public Timestamp getTimestamp() {

        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {

        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerPlacedBlock that = (PlayerPlacedBlock) o;

        return x == that.x && y == that.y && z == that.z && world.equals(that.world);
    }

    @Override
    public int hashCode() {

        int result = world.hashCode();
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }
}
