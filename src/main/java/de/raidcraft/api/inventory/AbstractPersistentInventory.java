package de.raidcraft.api.inventory;

import de.raidcraft.RaidCraft;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * @author Philip Urban
 */
public abstract class AbstractPersistentInventory implements PersistentInventory {

    private PersistentInventoryManager manager = null;
    private int id;
    private Inventory inventory;

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

        return id;
    }

    @Override
    public void save() {
        //TODO: implement
    }

    @Override
    public void delete() {
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
