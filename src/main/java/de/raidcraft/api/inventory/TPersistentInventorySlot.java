package de.raidcraft.api.inventory;

import javax.persistence.*;

/**
 * @author Philip Urban
 */
@Entity
@Table(name = "rcinv_slots")
public class TPersistentInventorySlot {

    @Id
    private int id;
    private int inventoryId;
    private int slot;
    private int objectId;
    @ManyToOne
    private TPersistentInventory inventory;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public int getInventoryId() {

        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {

        this.inventoryId = inventoryId;
    }

    public int getSlot() {

        return slot;
    }

    public void setSlot(int slot) {

        this.slot = slot;
    }

    public int getObjectId() {

        return objectId;
    }

    public void setObjectId(int objectId) {

        this.objectId = objectId;
    }

    public TPersistentInventory getInventory() {

        return inventory;
    }

    public void setInventory(TPersistentInventory inventory) {

        this.inventory = inventory;
    }
}
