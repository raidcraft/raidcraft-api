package de.raidcraft.api.inventory;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * @author Philip Urban
 */
@Entity
@Table(name = "rcinv_inventories")
public class TPersistentInventory {

    @Id
    private int id;
    private Timestamp created;
    private Timestamp lastUpdate;
    private int size;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
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
}
