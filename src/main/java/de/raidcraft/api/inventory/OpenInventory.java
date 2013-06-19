package de.raidcraft.api.inventory;

import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;

/**
 * @author Philip Urban
 */
public class OpenInventory {

    private PersistentInventory persistentInventory;
    private Map<String, Player> clients = new CaseInsensitiveMap<>();

    public OpenInventory(PersistentInventory persistentInventory) {

        this.persistentInventory = persistentInventory;
    }

    public void addClient(Player player) {

        clients.put(player.getName(), player);
    }

    public void removeClient(Player player) {

        clients.remove(player.getName());
    }

    public PersistentInventory getPersistentInventory() {

        return persistentInventory;
    }

    public Collection<Player> getClients() {

        return clients.values();
    }
}
