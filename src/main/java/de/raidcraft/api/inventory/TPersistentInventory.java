package de.raidcraft.api.inventory;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

/**
 * @author Philip Urban
 */
@Entity
@Table(name = "rcinv_inventories")
public class TPersistentInventory {

    @Id
    private int id;
    private String title;
    private Timestamp created;
    private Timestamp lastUpdate;
    private int size;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "inventory_id")
    private Set<TPersistentInventorySlot> slots;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public Timestamp getCreated() {

        return created;
    }

    public void setCreated(Timestamp created) {

        this.created = created;
    }

    public Timestamp getLastUpdate() {

        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {

        this.lastUpdate = lastUpdate;
    }

    public int getSize() {

        return size;
    }

    public void setSize(int size) {

        this.size = size;
    }

    public Set<TPersistentInventorySlot> getSlots() {

        return slots;
    }

    public void setSlots(Set<TPersistentInventorySlot> slots) {

        this.slots = slots;
    }
}
