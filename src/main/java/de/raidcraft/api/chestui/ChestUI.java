package de.raidcraft.api.chestui;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.chestui.menuitems.MenuItem;
import de.raidcraft.api.chestui.menuitems.MenuItemAPI;
import de.raidcraft.api.chestui.menuitems.MenuMinus;
import de.raidcraft.api.chestui.menuitems.MenuPlus;
import de.raidcraft.api.items.RcItems;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dragonfire
 */
public class ChestUI {

    private static ChestUI INSTANCE;
    private Plugin plugin;
    private Map<Inventory, Menu> cache = new HashMap<>();

    private ChestUI() {

        plugin = RaidCraft.getComponent(RaidCraftPlugin.class);
        //        Bukkit.getPluginManager().registerEvents(new Listener() {
        //            @EventHandler
        //            public void cmd(PlayerCommandPreprocessEvent event) {
        //                if (event.getMessage().contains("menu")) {
        //                    Menu m = new Menu("test menu from me");
        //                    m.addMenuItem(new MenuItem());
        //                    Menu m2 = new Menu("blalba");
        //                    m.addMenuItem(new OpenMenu(m2));
        //                    ChestUI.getInstance().openMenu(event.getPlayer(), m);
        //                }
        //            }
        //        }, plugin);
    }

    public static ChestUI getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new ChestUI();
        }
        return INSTANCE;
    }

    // max support 999 99 99
    public void openMoneySelection(Player player, String menu_name, double currentMoneyValue) {

        Menu menu = new Menu(menu_name);
        // GGG SS KK
        final MenuItemAPI g100 = new MenuItem(Material.GOLD_INGOT, "");
        final MenuItemAPI g10 = new MenuItem(Material.GOLD_INGOT, "");
        final MenuItemAPI g1 = new MenuItem(Material.GOLD_INGOT, "");

        final MenuItemAPI s10 = new MenuItem(Material.IRON_INGOT, "");
        final MenuItemAPI s1 = new MenuItem(Material.IRON_INGOT, "");

        final MenuItemAPI k10 = new MenuItem(Material.NETHER_BRICK_ITEM, "");
        final MenuItemAPI k1 = new MenuItem(Material.NETHER_BRICK_ITEM, "");

        // TODO: move into menuitem
        final int[] values = new int[]{1, 1, 1, 1, 1, 1, 1};
        final MenuItemAPI[] menu_items = new MenuItemAPI[]{
                g100, g10, g1, s10, s1, k10, k1};
        final ItemStack[] stacks = new ItemStack[]{g100.getItem(), g10.getItem(),
                g1.getItem(), s10.getItem(), s1.getItem(), k10.getItem(), k1.getItem()};
        final ItemStack[] empty = new ItemStack[]{
                RcItems.getGlassPane(DyeColor.YELLOW), RcItems.getGlassPane(DyeColor.YELLOW),
                RcItems.getGlassPane(DyeColor.YELLOW),
                RcItems.getGlassPane(DyeColor.WHITE), RcItems.getGlassPane(DyeColor.WHITE),
                RcItems.getGlassPane(DyeColor.BROWN), RcItems.getGlassPane(DyeColor.BROWN)};


        // +++ ++ ++
        menu.addMenuItem(new MenuPlus("+100 Gold") {
            @Override
            public void trigger(Player player) {

                delta(1, 0, values, stacks, menu_items, empty);
            }
        });
        menu.addMenuItem(new MenuPlus("+10 Gold") {
            @Override
            public void trigger(Player player) {

                delta(1, 1, values, stacks, menu_items, empty);
            }
        });
        menu.addMenuItem(new MenuPlus("+1 Gold") {
            @Override
            public void trigger(Player player) {

                delta(1, 2, values, stacks, menu_items, empty);
            }
        });
        menu.empty();
        menu.addMenuItem(new MenuPlus("+10 Silber") {
            @Override
            public void trigger(Player player) {

                delta(1, 3, values, stacks, menu_items, empty);
            }
        });
        menu.addMenuItem(new MenuPlus("+1 Silber") {
            @Override
            public void trigger(Player player) {

                delta(1, 4, values, stacks, menu_items, empty);
            }
        });
        menu.empty();
        menu.addMenuItem(new MenuPlus("+10 Kuper") {
            @Override
            public void trigger(Player player) {

                delta(1, 5, values, stacks, menu_items, empty);
            }
        });
        menu.addMenuItem(new MenuPlus("+1 Kuper") {
            @Override
            public void trigger(Player player) {

                delta(1, 6, values, stacks, menu_items, empty);
            }
        });

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
        menu.addMenuItem(new MenuMinus("-100 Gold") {
            @Override
            public void trigger(Player player) {

                delta(-1, 0, values, stacks, menu_items, empty);
            }
        });
        menu.addMenuItem(new MenuMinus("-10 Gold") {
            @Override
            public void trigger(Player player) {

                delta(-1, 1, values, stacks, menu_items, empty);
            }
        });
        menu.addMenuItem(new MenuMinus("-1 Gold") {
            @Override
            public void trigger(Player player) {

                delta(-1, 2, values, stacks, menu_items, empty);
            }
        });
        menu.empty();
        menu.addMenuItem(new MenuMinus("-10 Silber") {
            @Override
            public void trigger(Player player) {

                delta(-1, 3, values, stacks, menu_items, empty);
            }
        });
        menu.addMenuItem(new MenuMinus("-1 Silber") {
            @Override
            public void trigger(Player player) {

                delta(-1, 4, values, stacks, menu_items, empty);
            }
        });
        menu.empty();
        menu.addMenuItem(new MenuMinus("-10 Kuper") {
            @Override
            public void trigger(Player player) {

                delta(-1, 5, values, stacks, menu_items, empty);
            }
        });
        menu.addMenuItem(new MenuMinus("-1 Kuper") {
            @Override
            public void trigger(Player player) {

                delta(-1, 6, values, stacks, menu_items, empty);
            }
        });

        this.openMenu(player, menu);
    }

    public static void delta(int delta, int index, int[] values, ItemStack[] stacks, MenuItemAPI[] items, ItemStack[] empty) {

        int newValue = values[index] + delta;
        // if first item
        if (newValue == 1 && values[index] == 0) {
            values[index] = 1;
            items[index].setItem(stacks[index]);
            return;
        }
        if (newValue > 9) {
            return;
        }
        if (newValue < 1) {
            values[index] = 0;
            items[index].setItem(empty[index]);
            return;
        }
        values[index] += delta;
        items[index].getItem().setAmount(values[index]);
    }

    public void openMenu(Player player, Menu menu) {

        Inventory inv = menu.generateInvenntory(player);
        cache.put(inv, menu);
        Bukkit.getPluginManager().registerEvents(new RestrictInventory(player),
                plugin);
        player.openInventory(inv);
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
            cache.get(event.getInventory()).triggerMenuItem(event.getSlot(), (Player) holder);
        }

        @EventHandler
        public void close(InventoryCloseEvent event) {

            InventoryHolder holder = event.getInventory().getHolder();
            if (!(holder instanceof Player) || ((Player) holder) != player) {
                return;
            }
            HandlerList.unregisterAll(this);
            ((Player) holder).sendMessage("close inventory");
        }
    }
}
