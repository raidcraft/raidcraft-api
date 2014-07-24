package de.raidcraft.api.chestui;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.chestui.menuitems.MenuItemInteractive;
import de.raidcraft.api.chestui.menuitems.MenuMinus;
import de.raidcraft.api.chestui.menuitems.MenuPlus;
import de.raidcraft.api.items.RcItems;
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
 * @author Dragonfire
 */
public class ChestUI {

    private static ChestUI INSTANCE;
    private Plugin plugin;
    private Map<Player, Menu> cache = new HashMap<>();

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
        if(currentMoneyValue < 0) {
            // TODO: warning
            return;
        }
        final int[] values =  MathUtil.getDigits(currentMoneyValue, 2);

        Menu menu = new Menu(menu_name);
        // TODO: parse values


        // GGG SS KK
        final MenuItemInteractive g100 = new MenuItemInteractive(RcItems.createItem(
                Material.GOLD_INGOT, ""),
                RcItems.getGlassPane(DyeColor.YELLOW),
                1, 9);
        final MenuItemInteractive g10 = new MenuItemInteractive(RcItems.createItem(
                Material.GOLD_INGOT, ""),
                RcItems.getGlassPane(DyeColor.YELLOW),
                1, 9);
        final MenuItemInteractive g1 = new MenuItemInteractive(RcItems.createItem(
                Material.GOLD_INGOT, ""),
                RcItems.getGlassPane(DyeColor.YELLOW),
                1, 9);

        final MenuItemInteractive s10 = new MenuItemInteractive(RcItems.createItem(
                Material.IRON_INGOT, ""),
                RcItems.getGlassPane(DyeColor.WHITE),
                1, 9);
        final MenuItemInteractive s1 = new MenuItemInteractive(RcItems.createItem(
                Material.IRON_INGOT, ""),
                RcItems.getGlassPane(DyeColor.WHITE),
                1, 9);

        final MenuItemInteractive k10 = new MenuItemInteractive(RcItems.createItem(
                Material.NETHER_BRICK_ITEM, ""),
                RcItems.getGlassPane(DyeColor.BROWN),
                1, 9);
        final MenuItemInteractive k1 = new MenuItemInteractive(RcItems.createItem(
                Material.NETHER_BRICK_ITEM, ""),
                RcItems.getGlassPane(DyeColor.BROWN),
                1, 9);

        // +++ ++ ++
        menu.addMenuItem(new MenuPlus("+100 Gold") {
            @Override
            public void trigger(Player player) {

                g100.increase();
            }
        });
        menu.addMenuItem(new MenuPlus("+10 Gold") {
            @Override
            public void trigger(Player player) {

                g10.increase();
            }
        });
        menu.addMenuItem(new MenuPlus("+1 Gold") {
            @Override
            public void trigger(Player player) {

                g1.increase();
            }
        });
        menu.empty();
        menu.addMenuItem(new MenuPlus("+10 Silber") {
            @Override
            public void trigger(Player player) {

                s10.increase();
            }
        });
        menu.addMenuItem(new MenuPlus("+1 Silber") {
            @Override
            public void trigger(Player player) {

                s1.increase();
            }
        });
        menu.empty();
        menu.addMenuItem(new MenuPlus("+10 Kuper") {
            @Override
            public void trigger(Player player) {

                k10.increase();
            }
        });
        menu.addMenuItem(new MenuPlus("+1 Kuper") {
            @Override
            public void trigger(Player player) {

                k1.increase();
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

                g100.decrease();
            }
        });
        menu.addMenuItem(new MenuMinus("-10 Gold") {
            @Override
            public void trigger(Player player) {

                g10.decrease();
            }
        });
        menu.addMenuItem(new MenuMinus("-1 Gold") {
            @Override
            public void trigger(Player player) {

                g1.decrease();
            }
        });
        menu.empty();
        menu.addMenuItem(new MenuMinus("-10 Silber") {
            @Override
            public void trigger(Player player) {

                s10.decrease();
            }
        });
        menu.addMenuItem(new MenuMinus("-1 Silber") {
            @Override
            public void trigger(Player player) {

                s1.decrease();
            }
        });
        menu.empty();
        menu.addMenuItem(new MenuMinus("-10 Kuper") {
            @Override
            public void trigger(Player player) {

                k10.decrease();
            }
        });
        menu.addMenuItem(new MenuMinus("-1 Kuper") {
            @Override
            public void trigger(Player player) {

                k1.decrease();
            }
        });
        this.openMenu(player, menu);
    }

    public void openMenu(Player player, Menu menu) {

        Inventory inv = menu.generateInvenntory(player);
        cache.put(player, menu);
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
