package de.raidcraft;

import de.raidcraft.api.database.Bean;
import de.raidcraft.api.database.Database;
import org.bukkit.block.Block;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
* @author Silthus
*/
@Entity
@Table(name = "rc_player_placed_blocks")
public class PlayerPlacedBlock implements Bean {

    @Id
    private int id;
    private String world;
    private int x;
    private int y;
    private int z;

    public PlayerPlacedBlock() {

    }

    public PlayerPlacedBlock(Block block) {

        this(block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
    }

    public PlayerPlacedBlock(String world, int x, int y, int z) {

        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        Database.save(this);
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
