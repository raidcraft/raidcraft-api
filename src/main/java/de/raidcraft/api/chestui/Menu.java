package de.raidcraft.api.chestui;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.chestui.menuitems.MenuItem;
import de.raidcraft.api.chestui.menuitems.MenuItemAPI;
import de.raidcraft.api.chestui.menuitems.MenuItemHide;
import de.raidcraft.api.inventory.RC_Inventory;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * A Menu for the ChestUI.
 * Add MenuItems to the Menu.
 * A Menu has pages and a toolbar.
 * You cann add infinity items to a menu, it seperate it
 * automaticlly into pages.
 * MenuItem filled in Minecraft order, left top to right bottom line by line.
 *
 * @author Dragonfire
 */
public class Menu {

    private String name;
    private List<MenuItemAPI> items = new ArrayList<>();
    private InventoryContainer[] invs;
    private Inventory inventory;
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
    private MenuItemHide toolbar_back = (MenuItemHide) new MenuItemHide() {
        @Override
        public void trigger(Player player) {

            lastPage();
        }
    }.setVisibleItem(MenuItemAPI.getItemPlus());
    private MenuItemHide toolbar_forward = (MenuItemHide) new MenuItemHide() {
        @Override
        public void trigger(Player player) {

            nextPage();
        }
    }.setVisibleItem(MenuItemAPI.getItemMinus());
    private MenuItemAPI toolbar_site = new MenuItem().setItem(MenuItemAPI.getItemPage());


    public Menu(String name, boolean toolbar) {

        this.name = name;
        this.toolbarActive = toolbar;
    }

    public Menu(String name) {

        this(name, false);
    }

    public void empty() {

        addMenuItem(new MenuItem());
    }

    public void close() {
        this.player.closeInventory();
    }

    public Inventory generateInvenntory(Player player) {

        this.player = player;


        extraRows = (isToolbarActive() ? 1 : 0);
        availableRows = (int) Math.ceil(items.size() / (double) RC_Inventory.COLUMN_COUNT);
        // if to big, shrink
        if (availableRows + extraRows > RC_Inventory.MAX_ROWS) {
            // activate toolbar
            setToolbarActive(true);
            extraRows = 1;

            availableRows = RC_Inventory.MAX_ROWS - extraRows;
        }

        availableSlots = availableRows * RC_Inventory.COLUMN_COUNT;

        allRows = availableRows + extraRows; // included toolbar
        allSlots = allRows * RC_Inventory.COLUMN_COUNT;

        // create inventory
        inventory = Bukkit.createInventory(player, allSlots, name);

        int menus = (int) Math.ceil(items.size() / (double) availableSlots);
        invs = new InventoryContainer[menus];
        menus_api = new MenuItemAPI[menus][allSlots];

        for (int page = 0; page < getPageCount(); page++) {
            invs[page] = generateInventory(page);
        }

        if (this.isToolbarActive()) {
            this.generateToolbar();
        }

        page = 0;
        showPage(0);
        return inventory;
    }

    protected InventoryContainer generateInventory(int render_page) {

        InventoryContainer container = new InventoryContainer(availableSlots);
        int slot = 0;
        int n = render_page * allSlots + availableSlots;
        for (int item_index = render_page * allSlots; item_index < n; item_index++) {
            // if no item, let empty
            if (items.size() <= item_index) {
                break;
            }
            container.setItem(slot, items.get(item_index).getItem());
            setItemData(slot, items.get(item_index));
            menus_api[render_page][slot] = items.get(item_index);
            slot++;
        }
        return container;
    }

    private void setItemData(int slot, MenuItemAPI menu_item) {

        menu_item.setSlot(slot);
        menu_item.setInventory(inventory);
    }

    protected void generateToolbar() {

        int start = availableRows * RC_Inventory.COLUMN_COUNT;
        if (toolbar_cancel != null) {
            int slot = start;
            inventory.setItem(slot, toolbar_cancel.getItem());
            setItemData(slot, toolbar_cancel);
            menus_api[0][slot] = toolbar_cancel;
        }
        if (toolbar_ok != null) {
            int slot = start + RC_Inventory.COLUMN_COUNT - 1;
            inventory.setItem(slot, toolbar_ok.getItem());
            setItemData(slot, toolbar_ok);
            menus_api[0][slot] = toolbar_ok;
        }

        if (toolbar_site != null) {
            int slot = start + 4;
            inventory.setItem(slot, toolbar_site.getItem());
            setItemData(slot, toolbar_site);
            menus_api[0][slot] = toolbar_site;
        }

        if (toolbar_back != null) {
            int slot = start + 1;
            inventory.setItem(slot, toolbar_back.getItem());
            setItemData(slot, toolbar_back);
            menus_api[0][slot] = toolbar_back;
        }
        if (toolbar_forward != null) {
            int slot = start + RC_Inventory.COLUMN_COUNT - 2;
            inventory.setItem(slot, toolbar_forward.getItem());
            setItemData(slot, toolbar_forward);
            menus_api[0][slot] = toolbar_forward;
        }
    }

    public MenuItemAPI getMenuItem(int menuPage, int slot) {
        // if toolbar
        if (slot > availableSlots) {
            return menus_api[0][slot];
        }
        return menus_api[menuPage][slot];
    }

    public int getCurrentPageIndex() {

        return page;
    }

    public void showPage(int newpage) {
        // convert to indexs
        if (newpage < 0 || newpage >= invs.length) {
            RaidCraft.getComponent(RaidCraftPlugin.class).getLogger().warning(
                    name + " not a valid page: " + (newpage + 1));
            return;
        }
        page = newpage;
        // set back/forward
        this.toolbar_back.toggle(page <= 0);
        this.toolbar_forward.toggle(page >= getPageCount() - 1);
        this.toolbar_site.getItem().setAmount(page + 1);

        ItemStack[] newItems = invs[page].getContent();
        for (int slot = 0; slot < newItems.length; slot++) {
            // clear old items
            if (newItems[slot] == null) {
                inventory.clear(slot);
            }
            inventory.setItem(slot, newItems[slot]);
        }
    }

    public void nextPage() {

        showPage(getCurrentPageIndex() + 1);
    }

    public void lastPage() {

        showPage(getCurrentPageIndex() - 1);
    }

    public void triggerMenuItem(int slot, Player player) {

        if (getMenuItem(page, slot) == null) {
            return;
        }
        getMenuItem(page, slot).trigger(player);
    }

    public void addMenuItem(MenuItemAPI item) {

        items.add(item);
    }

    public int getPageCount() {

        return invs.length;
    }

    public class InventoryContainer {

        @Getter
        private ItemStack[] content;

        public InventoryContainer(int slots) {

            content = new ItemStack[slots];
        }

        public void setItem(int slot, ItemStack item) {

            this.content[slot] = item;
        }
    }

}
