package de.raidcraft.api.inventory;

import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Philip Urban
 */
public class PersistentInventoryManager implements Component {

    private RaidCraftPlugin plugin;
    private Map<Inventory, OpenInventory> openedInventories = new HashMap<>();

    public PersistentInventoryManager(RaidCraftPlugin plugin) {

        this.plugin = plugin;
        plugin.registerEvents(new InventoryListener());
    }

    public PersistentInventory getOpenedInventory(Inventory inventory) {

        OpenInventory openInventory = openedInventories.get(inventory);
        if(openInventory != null) {
            return openInventory.getPersistentInventory();
        }
        return null;
    }

    public void openInventory(Player player, PersistentInventory persistentInventory) {

        if(openedInventories.containsKey(persistentInventory.getInventory())) {
            openedInventories.get(persistentInventory.getInventory()).addClient(player);
        }
        else {
            OpenInventory openInventory = new OpenInventory(persistentInventory);
            openInventory.addClient(player);
            openedInventories.put(persistentInventory.getInventory(), openInventory);
        }
    }

    public void closeInventory(Player player, PersistentInventory persistentInventory) {

        OpenInventory openInventory = openedInventories.get(persistentInventory.getInventory());
        if(openInventory == null) return;

        openInventory.removeClient(player);
        if(openInventory.getClients().size() == 0) {
            openedInventories.remove(persistentInventory.getInventory());
        }
        player.closeInventory();
    }

    public void closeInventory(PersistentInventory persistentInventory) {

        OpenInventory openInventory = openedInventories.get(persistentInventory.getInventory());
        if(openInventory == null) return;

        openedInventories.remove(persistentInventory.getInventory());
        for(Player player : openInventory.getClients()) {

            player.closeInventory();
        }
    }
}
