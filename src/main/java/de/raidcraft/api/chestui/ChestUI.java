package de.raidcraft.api.chestui;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.chestui.menuitems.MenuItemAPI;
import de.raidcraft.api.chestui.menuitems.MenuItemInteractive;
import de.raidcraft.api.items.RC_Items;
import de.raidcraft.util.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * SINGLETON
 * Global Class for creating Chest UI's.
 * Create a Menu and use this class to open it.
 *
 * @author Dragonfire
 */
public class ChestUI {

    private static ChestUI INSTANCE;
    private Plugin plugin;
    private Map<Player, Menu> cache = new HashMap<>();

    private ChestUI() {

        plugin = RaidCraft.getComponent(RaidCraftPlugin.class);
    }

    public static ChestUI getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new ChestUI();
        }
        return INSTANCE;
    }

    public void openMenu(Player player, Menu menu) {

        Inventory inv = menu.generateInvenntory(player);
        cache.put(player, menu);
        Bukkit.getPluginManager().registerEvents(new RestrictInventory(player),
                plugin);
        player.openInventory(inv);
    }


    // max support 999 99 99
    public void openMoneySelection(Player player, String menu_name, double currentMoneyValue) {

        if (currentMoneyValue < 0) {
            // TODO: warning
            return;
        }
        final int[] values = MathUtil.getDigits(currentMoneyValue, 2);

        Menu menu = new Menu(menu_name);
        // TODO: parse values


        // GGG SS KK
        final MenuItemInteractive g100 = new MenuItemInteractive(RC_Items.createItem(
                Material.GOLD_INGOT, ""),
                RC_Items.getGlassPane(DyeColor.YELLOW),
                1, 9);
        final MenuItemInteractive g10 = new MenuItemInteractive(RC_Items.createItem(
                Material.GOLD_INGOT, ""),
                RC_Items.getGlassPane(DyeColor.YELLOW),
                1, 9);
        final MenuItemInteractive g1 = new MenuItemInteractive(RC_Items.createItem(
                Material.GOLD_INGOT, ""),
                RC_Items.getGlassPane(DyeColor.YELLOW),
                1, 9);

        final MenuItemInteractive s10 = new MenuItemInteractive(RC_Items.createItem(
                Material.IRON_INGOT, ""),
                RC_Items.getGlassPane(DyeColor.WHITE),
                1, 9);
        final MenuItemInteractive s1 = new MenuItemInteractive(RC_Items.createItem(
                Material.IRON_INGOT, ""),
                RC_Items.getGlassPane(DyeColor.WHITE),
                1, 9);

        final MenuItemInteractive k10 = new MenuItemInteractive(RC_Items.createItem(
                Material.NETHER_BRICK_ITEM, ""),
                RC_Items.getGlassPane(DyeColor.BROWN),
                1, 9);
        final MenuItemInteractive k1 = new MenuItemInteractive(RC_Items.createItem(
                Material.NETHER_BRICK_ITEM, ""),
                RC_Items.getGlassPane(DyeColor.BROWN),
                1, 9);

        // +++ ++ ++
        menu.addMenuItem((new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                g100.increase();
            }
        }).setItem(MenuItemAPI.getItemPlus("+100 Gold")));
        menu.addMenuItem((new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                g10.increase();
            }
        }).setItem(MenuItemAPI.getItemPlus("+10 Gold")));
        menu.addMenuItem((new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                g1.increase();
            }
        }).setItem(MenuItemAPI.getItemPlus("+1 Gold")));
        menu.empty();
        menu.addMenuItem((new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                s10.increase();
            }
        }).setItem(MenuItemAPI.getItemPlus("+10 Silber")));
        menu.addMenuItem((new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                s1.increase();
            }
        }).setItem(MenuItemAPI.getItemPlus("+1 Silber")));
        menu.empty();
        menu.addMenuItem((new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                k10.increase();
            }
        }).setItem(MenuItemAPI.getItemPlus("+10 Bronze")));
        menu.addMenuItem((new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                k1.increase();
            }
        }).setItem(MenuItemAPI.getItemPlus("+1 Bronze")));

        // GGG SSS KK
        menu.addMenuItem(g100);
        menu.addMenuItem(g10);
        menu.addMenuItem(g1);
        menu.empty();
        menu.addMenuItem(s10);
        menu.addMenuItem(s1);
        menu.empty();
        menu.addMenuItem(k10);
        menu.addMenuItem(k1);

        // --- -- --
        menu.addMenuItem((new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                g100.decrease();
            }
        }).setItem(MenuItemAPI.getItemPlus("-100 Gold")));
        menu.addMenuItem((new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                g10.decrease();
            }
        }).setItem(MenuItemAPI.getItemPlus("-10 Gold")));
        menu.addMenuItem((new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                g1.decrease();
            }
        }).setItem(MenuItemAPI.getItemPlus("-1 Gold")));
        menu.empty();
        menu.addMenuItem((new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                s10.decrease();
            }
        }).setItem(MenuItemAPI.getItemPlus("-10 Silber")));
        menu.addMenuItem((new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                s1.decrease();
            }
        }).setItem(MenuItemAPI.getItemPlus("-1 Silber")));
        menu.empty();
        menu.addMenuItem((new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                k10.decrease();
            }
        }).setItem(MenuItemAPI.getItemPlus("-10 Kuper")));
        menu.addMenuItem((new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                k1.decrease();
            }
        }).setItem(MenuItemAPI.getItemPlus("-1 Kuper")));
        this.openMenu(player, menu);
    }

    public class RestrictInventory implements Listener {

        private Player player;

        public RestrictInventory(Player player) {

            this.player = player;
        }

        @EventHandler
        public void interact(InventoryClickEvent event) {

            InventoryHolder holder = event.getInventory().getHolder();
            if (!(holder instanceof Player) || ((Player) holder) != player) {
                return;
            }
            event.setCancelled(true);
            // test if clicked outside the inventory and empty slot
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            // call custom event
            cache.get(((Player) holder)).triggerMenuItem(event.getSlot(), (Player) holder);
        }

        @EventHandler
        public void close(InventoryCloseEvent event) {

            InventoryHolder holder = event.getInventory().getHolder();
            if (!(holder instanceof Player) || ((Player) holder) != player) {
                return;
            }
            HandlerList.unregisterAll(this);
        }
    }
}
