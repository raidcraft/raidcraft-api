package de.raidcraft.tables;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.ebean.BaseModel;
import io.ebean.EbeanServer;
import io.ebean.annotation.Index;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "rc_minecraft_items")
public class TMinecraftItem extends BaseModel {

    /**
     * Creates or updates the given material in the database.
     *
     * @param material to create or update
     * @return true if item was created or updated
     */
    public static boolean createOrUpdate(Material material) {
        TMinecraftItem item = RaidCraft.getDatabase(RaidCraftPlugin.class).find(TMinecraftItem.class).where()
                .eq("key", material.getKey().toString())
                .findOne();

        if (item == null) {
            item = new TMinecraftItem();
            item.since = Bukkit.getVersion();
            item.name = new ItemStack(material).getI18NDisplayName();
            item.blockData = material.createBlockData().getAsString();
            item.save();
            return true;
        }

        return false;
    }

    @Index(unique = true)
    private String key;

    private String name;

    @Column(length = 1024)
    private String blockData;

    private String since;

    private String deprecatedSince;

    @Override
    protected EbeanServer database() {

        return RaidCraft.getDatabase(RaidCraftPlugin.class);
    }
}
