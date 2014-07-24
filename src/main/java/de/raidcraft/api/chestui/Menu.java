package de.raidcraft.api.chestui;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.chestui.menuitems.MenuItem;
import de.raidcraft.api.chestui.menuitems.MenuItemAPI;
import de.raidcraft.api.inventory.RcInventory;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
    private Inventory[] invs;
    private MenuItemAPI[][] menus_api;
    private int page;
    @Getter
    @Setter
    private boolean toolbarActive = false;
    private Player player;

    private int availableRows = -1;
    private int availableSlots = -1;
    private int allRows = -1;
    private int allSlots = -1;
    private int extraRows = -1;


    // toolbar
    private MenuItemAPI toolbar_ok;
    private MenuItemAPI toolbar_cancel;
    private MenuItemAPI toolbar_back = new MenuItem(Material.ARROW, "Back") {
        @Override
        public void trigger(Player player) {

            lastPage();
        }
    };
    private MenuItemAPI toolbar_forward = new MenuItem(Material.ARROW, "Forward") {
        @Override
        public void trigger(Player player) {

            nextPage();
        }
    };


    public Menu(String name, boolean toolbar) {

        this.name = name;
        this.toolbarActive = toolbar;
    }

    public Menu(String name) {

        this(name, false);
    }

    private void startGroup() {

        this.startGroups.add(items.size());
    }

    private void endGroup() {

        this.endGroups.add(items.size());
    }

    public void empty() {

        addMenuItem(new MenuItem());
    }

    public Inventory generateInvenntory(Player player) {

        this.player = player;


        extraRows = (isToolbarActive() ? 1 : 0);
        availableRows = (int) Math.ceil(items.size() / (double) RcInventory.COLUMN_COUNT);
        // if to big, shrink
        if (availableRows + extraRows > RcInventory.MAX_ROWS) {
            // activate toolbar
            setToolbarActive(true);
            extraRows = 1;

            availableRows = RcInventory.MAX_ROWS - extraRows;
        }

        availableSlots = availableRows * RcInventory.COLUMN_COUNT;

        allRows = availableRows + extraRows; // included toolbar
        allSlots = allRows * RcInventory.COLUMN_COUNT;

        int menus = (int) Math.ceil(items.size() / (double) availableSlots);
        invs = new Inventory[menus];
        menus_api = new MenuItemAPI[menus][allSlots];

        for (int page = 0; page < getPageCount(); page++) {
            invs[page] = generateInventory(page);
        }

        page = 0;
        return invs[page];
    }

    protected Inventory generateInventory(int render_page) {

        Inventory inventory = Bukkit.createInventory(player,
                allSlots, name + ": Seite " + (render_page + 1) + " von " + getPageCount());
        int slot = 0;
        int n = render_page * allSlots + availableSlots;
        for (int item_index = render_page * allSlots; item_index < n; item_index++) {
            // if no item, let empty
            if (items.size() <= item_index) {
                break;
            }
            inventory.setItem(slot, items.get(item_index).getItem());
            items.get(item_index).setSlot(slot);
            items.get(item_index).setInventory(inventory);
            menus_api[render_page][slot] = items.get(item_index);
            slot++;
        }
        if (this.isToolbarActive()) {
            this.generateToolbar(availableRows, inventory, render_page);
        }
        return inventory;
    }

    protected void generateToolbar(int row, Inventory inventory, int page) {

        int start = row * RcInventory.COLUMN_COUNT;
        if (toolbar_cancel != null) {
            inventory.setItem(start, toolbar_cancel.getItem());
            menus_api[page][start] = toolbar_cancel;
        }
        if (toolbar_ok != null) {
            inventory.setItem(start + RcInventory.COLUMN_COUNT - 1, toolbar_ok.getItem());
            menus_api[page][start + RcInventory.COLUMN_COUNT - 1] = toolbar_ok;
        }

        if (toolbar_back != null) {
            inventory.setItem(start + 1, toolbar_back.getItem());
            menus_api[page][start + 1] = toolbar_back;
        }
        if (toolbar_forward != null) {
            inventory.setItem(start + RcInventory.COLUMN_COUNT - 2, toolbar_forward.getItem());
            menus_api[page][start + RcInventory.COLUMN_COUNT - 2] = toolbar_forward;
        }
    }

    public int getCurrentPageIndex() {

        return page;
    }

    public void showPage(int newpage) {
        // convert to indexs
        if (newpage < 0 || newpage >= invs.length) {
            RaidCraft.getComponent(RaidCraftPlugin.class).getLogger().warning(
                    name + " not a valdi page: " + (newpage + 1));
            return;
        }
        page = newpage;
        ChestUI.getInstance().switchMenu(player, invs[page]);
    }

    public void nextPage() {

        showPage(getCurrentPageIndex() + 1);
    }

    public void lastPage() {

        showPage(getCurrentPageIndex() - 1);
    }

    public void triggerMenuItem(int slot, Player player) {

        if (menus_api[page][slot] == null) {
            return;
        }
        menus_api[page][slot].trigger(player);
    }

    public void addMenuItem(MenuItemAPI item) {

        items.add(item);
    }

    public int getPageCount() {

        return invs.length;
    }

}
