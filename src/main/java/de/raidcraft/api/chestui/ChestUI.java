package de.raidcraft.api.chestui;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
        Bukkit.getPluginManager().registerEvents(new RestrictInventoryListener(), plugin);
    }

    public static ChestUI getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new ChestUI();
        }
        return INSTANCE;
    }

    public void openMenu(Player player, Menu menu) {

        openMenu(player, menu, null);
    }

    public void openMenu(final Player player, final Menu menu, final MenuListener listener) {
        // close inventory to prevent cache clear
        player.closeInventory();
        menu.setListener(listener);
        Inventory inv = menu.generateInvenntory(player);
        cache.put(player, menu);
        player.openInventory(inv);
    }

    public void selectItem(Player player, String name, ItemSelectorListener listener) {

        ItemSelector.getInstance().open(player, name, listener);
    }


    // max support 999 99 99
    public void openMoneySelection(Player player, String menu_name, double currentMoneyValue,
                                   MoneySelectorListener listener) {

        MoneySelector.getInstance().openMoneySelection(player, menu_name, currentMoneyValue, listener);
    }

    public class RestrictInventoryListener implements Listener {

        @EventHandler
        public void interact(InventoryClickEvent event) {

            if (!(event.getInventory().getHolder() instanceof Player)) {
                return;
            }
            Player player = (Player) event.getInventory().getHolder();
            if (cache.get(player) == null) {
                return;
            }
            event.setCancelled(true);
            // test if clicked outside the inventory and empty slot
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            // call custom event
            cache.get(player).triggerMenuItem(event.getSlot(), player);
        }

        @EventHandler
        public void close(InventoryCloseEvent event) {

            InventoryHolder holder = event.getInventory().getHolder();
            if (!(holder instanceof Player) || (cache.get((Player) holder)) == null) {
                return;
            }
            Menu menu = cache.remove(event.getInventory().getHolder());
            if (menu != null && menu.getListener() != null && !menu.getListener().isAccepted()) {
                menu.getListener().cancel();
            }
        }
    }
}
