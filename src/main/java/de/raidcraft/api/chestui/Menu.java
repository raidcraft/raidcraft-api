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
    private List<Integer> startGroups = new ArrayList<>();
    private List<Integer> endGroups = new ArrayList<>();
    private MenuItemAPI[] inv;

    public Menu(String name) {

        this.name = name;
    }

    private void startGroup() {

        this.startGroups.add(items.size());
    }

    private void endGroup() {

        this.endGroups.add(items.size());
    }

    public Inventory generateInvenntory(Player player) {

        int size = 9 * 6;
        this.inv = new MenuItemAPI[size];
        final Inventory inventory = Bukkit.createInventory(player, size,
                name);
        for (int i = 0; i < items.size(); i++) {
            inv[i] = items.get(i);
            inventory.setItem(i, inv[i].getItem());
        }
        return inventory;
    }

    public void triggerMenuItem(int slot, Player player) {

        if (inv[slot] == null) {
            return;
        }
        inv[slot].trigger(player);
    }

    public void addMenuItem(MenuItemAPI item) {

        items.add(item);
    }

}
