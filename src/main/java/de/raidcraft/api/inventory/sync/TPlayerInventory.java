package de.raidcraft.api.inventory.sync;

import com.avaje.ebean.validation.NotNull;
import de.raidcraft.api.storage.ObjectStorage;
import lombok.Data;
import net.minecraft.server.v1_8_R3.Items;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
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
    @Version
    @NotNull
    private UUID player;
    @NotNull
    private int inventoryId;
    private int objectHelmet = -1;
    private int objectChestplate = -1;
    private int objectLeggings = -1;
    private int objectBoots = -1;
    private float exp = -1;
    private int level = -1;
    private boolean locked;
    private Date createdAt;
    private Date updatedAt;

    public void setArmor(PlayerInventory playerInventory, ObjectStorage<ItemStack> armorStorage) {
        if (playerInventory == null) {
            throw new IllegalArgumentException(player + ": playerInventory should not be null");
        }
        if (armorStorage == null) {
            throw new IllegalArgumentException(player + ": armorStorage should not be null");
        }

        setObjectHelmet(armorStorage.storeObject(itemOrAir(playerInventory.getHelmet())));
        setObjectChestplate(armorStorage.storeObject(itemOrAir(playerInventory.getChestplate())));
        setObjectLeggings(armorStorage.storeObject(itemOrAir(playerInventory.getLeggings())));
        setObjectBoots(armorStorage.storeObject(itemOrAir(playerInventory.getBoots())));
    }

    private ItemStack itemOrAir(ItemStack item) {
        return (item == null) ? new ItemStack(Material.AIR) : item;
    }
}
