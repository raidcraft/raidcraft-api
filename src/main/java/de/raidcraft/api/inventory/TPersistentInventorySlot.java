package de.raidcraft.api.inventory;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Philip Urban
 */
@Entity
@Table(name = "rcinv_slots")
public class TPersistentInventorySlot {

    @Id
    private int id;
    @ManyToOne
    private TPersistentInventory inventory;
    private int objectId;
    private int slot;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
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
