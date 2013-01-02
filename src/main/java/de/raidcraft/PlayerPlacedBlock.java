package de.raidcraft;

import com.avaje.ebean.Ebean;
import de.raidcraft.api.database.Bean;
import de.raidcraft.api.database.Database;
import org.bukkit.Bukkit;
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

    public PlayerPlacedBlock(Block block) {

        setX(block.getX());
        setY(block.getY());
        setZ(block.getZ());
        setWorld(block.getWorld().getName());
        Database.save(this);
    }

    @Id
    private int id;
    private int x;
    private int y;
    private int z;
    private String world;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
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

    public String getWorld() {

        return world;
    }

    public void setWorld(String world) {

        this.world = world;
    }

    public void remove() {

        Ebean.delete(this);
    }

    public Block getBlock() {

        return Bukkit.getWorld(getWorld()).getBlockAt(getX(), getY(), getZ());
    }
}
