package de.raidcraft.api.inventory.sync;

import com.avaje.ebean.validation.NotNull;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;

/**
 * @author Dragonfire
 */
@Data
@Entity
@Table(name = "rc_player_inventories")
public class TPlayerInventory {

    @Id
    private int id;
    @NotNull
    private UUID player;
    @NotNull
    private int inventoryId;
}
