package de.raidcraft.api.inventory;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * @author Philip Urban
 */
public abstract class AbstractPersistentInventory implements PersistentInventory {

    private PersistentInventoryManager manager = null;
    private Inventory inventory;

    private TPersistentInventory tPersistentInventory;

    protected AbstractPersistentInventory(TPersistentInventory tPersistentInventory, String title) {

        this.tPersistentInventory = tPersistentInventory;
        this.inventory = Bukkit.createInventory(null, tPersistentInventory.getSize(), title);
    }

    private PersistentInventoryManager getManager() {

        if(manager == null) {
            manager = RaidCraft.getComponent(PersistentInventoryManager.class);
        }
        return manager;
    }

    @Override
    public Inventory getInventory() {

        return inventory;
    }

    @Override
    public int getId() {

        return tPersistentInventory.getId();
    }

    @Override
    public void save() {
        //TODO: implement
    }

    @Override
    public void delete() {

        EbeanServer database = RaidCraft.getDatabase(RaidCraftPlugin.class);

        //TODO: implement
    }

    @Override
    public void open(Player player) {

        getManager().openInventory(player, this);
        player.openInventory(inventory);
    }

    @Override
    public void close(boolean save) {

        getManager().closeInventory(this);
        if(save) {
            save();
        }
    }
}
