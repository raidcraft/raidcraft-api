package de.raidcraft.api.chestui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dragonfire
 */
public class Menu {
    private String name;
    private List<MenuItemAPI> items = new ArrayList<>();
    private MenuLayout layout = null;

    public Menu(String name) {
        this.name = name;
    }

    public Inventory generateInvenntory(Player player) {
        final Inventory inventory = Bukkit.createInventory(player, 9 * 6,
                name);
        for(int i = 0; i < items.size(); i++) {
            inventory.setItem(i, items.get(i).getItem());
        }
        return inventory;
    }

    public void triggerMenuItem(int slot)  {
        Bukkit.broadcastMessage("trigger: " + slot);
    }

    public void addMenuItem(MenuItemAPI item) {
        items.add(item);
    }

}
