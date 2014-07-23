package de.raidcraft.api.chestui;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import org.bukkit.Bukkit;
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
        // +++ ++ ++
        menu.addMenuItem(MenuItemAPI.getPlus("+100 Gold"));
        menu.addMenuItem(MenuItemAPI.getPlus("+10 Gold"));
        menu.addMenuItem(MenuItemAPI.getPlus("+1 Gold"));
        menu.empty();
        menu.addMenuItem(MenuItemAPI.getPlus("+10 Silber"));
        menu.addMenuItem(MenuItemAPI.getPlus("+1 Silber"));
        menu.empty();
        menu.addMenuItem(MenuItemAPI.getPlus("+10 Kupfer"));
        menu.addMenuItem(MenuItemAPI.getPlus("+1 Kupfer"));

        // GGG SS KK
        menu.addMenuItem(new MenuItem(Material.GOLD_INGOT));
        menu.addMenuItem(new MenuItem(Material.GOLD_INGOT));
        menu.addMenuItem(new MenuItem(Material.GOLD_INGOT));
        menu.empty();
        menu.addMenuItem(new MenuItem(Material.IRON_INGOT));
        menu.addMenuItem(new MenuItem(Material.IRON_INGOT));
        menu.empty();
        menu.addMenuItem(new MenuItem(Material.NETHER_BRICK));
        menu.addMenuItem(new MenuItem(Material.NETHER_BRICK));

        // --- -- --
        menu.addMenuItem(MenuItemAPI.getMinus("-100 Gold"));
        menu.addMenuItem(MenuItemAPI.getMinus("-10 Gold"));
        menu.addMenuItem(MenuItemAPI.getMinus("-1 Gold"));
        menu.empty();
        menu.addMenuItem(MenuItemAPI.getMinus("-10 Silber"));
        menu.addMenuItem(MenuItemAPI.getMinus("-1 Silber"));
        menu.empty();
        menu.addMenuItem(MenuItemAPI.getMinus("-10 Kupfer"));
        menu.addMenuItem(MenuItemAPI.getMinus("-1 Kupfer"));

        this.openMenu(player, menu);
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
            ((Player) holder).closeInventory();
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
