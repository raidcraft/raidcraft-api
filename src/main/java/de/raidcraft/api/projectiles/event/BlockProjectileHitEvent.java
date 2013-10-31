package de.raidcraft.api.projectiles.event;

import de.raidcraft.api.projectiles.projectile.CustomProjectile;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;

/**
 * BlockProjectileHitEvent is fired when falling block projectile hits entity or
 * block.
 */
public class BlockProjectileHitEvent extends CustomProjectileHitEvent {

    private int mat;
    private int data;

    /**
     * Instantiates a new block projectile hit event.
     *
     * @param pro  projectile
     * @param b    hit block
     * @param f    block face
     * @param mat  block id
     * @param data damage value of block
     */
    public BlockProjectileHitEvent(CustomProjectile pro, Block b, BlockFace f, int mat, int data) {

        super(pro, b, f);
        this.mat = mat;
        this.data = data;
    }

    /**
     * Instantiates a new block projectile hit event.
     *
     * @param pro  projectile
     * @param ent  hit entity
     * @param mat  block id
     * @param data damage value of block
     */
    public BlockProjectileHitEvent(CustomProjectile pro, LivingEntity ent, int mat, int data) {

        super(pro, ent);
        this.mat = mat;
        this.data = data;
    }

    /**
     * Gets the block id.
     *
     * @return the block id
     */
    public int getBlockId() {

        return mat;
    }

    /**
     * Gets the data.
     *
     * @return damage value of block
     */
    public int getData() {

        return data;
    }

}
