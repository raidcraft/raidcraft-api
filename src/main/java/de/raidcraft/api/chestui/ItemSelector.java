package de.raidcraft.api.chestui;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.chestui.menuitems.MenuItemAPI;
import de.raidcraft.api.inventory.RC_Inventory;
import de.raidcraft.api.items.RC_Items;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;

/**
 * @author Dragonfire
 */
public class ItemSelector {

    private static ItemSelector INSTANCE;
    private Plugin plugin;

    private ItemSelector() {

        this.plugin = RaidCraft.getComponent(RaidCraftPlugin.class);
    }

    public static ItemSelector getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new ItemSelector();
        }
        return INSTANCE;
    }

    public void open(Player player, String name, ItemSelectorListener listener) {

        Inventory inventory = Bukkit.createInventory(player, 9 * 3, name);
        for (int i = 0; i < RC_Inventory.COLUMN_COUNT * 3; i++) {
            if (i == RC_Inventory.COLUMN_COUNT + 4) {
                continue;
            }
            inventory.setItem(i, RC_Items.getGlassPane(DyeColor.RED));
            inventory.setItem(inventory.getSize() - 1, MenuItemAPI.getItemOk());
            inventory.setItem(inventory.getSize() - RC_Inventory.COLUMN_COUNT, MenuItemAPI.getItemCancel());
        }
        player.openInventory(inventory);
        Bukkit.getPluginManager().registerEvents(new ItemSelectorListsener(player, inventory, listener), plugin);

    }

    public class ItemSelectorListsener implements Listener {

        private Player player;
        private ItemSelectorListener listener;
        private Inventory inventory;
        private boolean accept = false;
        int selectedSlot = -1;

        public ItemSelectorListsener(Player player, Inventory inventory, ItemSelectorListener listener) {

            this.player = player;
            this.inventory = inventory;
            this.listener = listener;
        }

        @EventHandler
        public void interact(InventoryClickEvent event) {

            InventoryHolder holder = event.getInventory().getHolder();
            if (!(holder instanceof Player) || ((Player) holder) != player) {
                return;
            }
            event.setCancelled(true);
            // only react on pickup events
            if (event.getAction() != InventoryAction.PICKUP_ALL) {
                return;
            }
            int slot = event.getSlot();
            // if player inventory select, rawslot coutns from left top to right bottom of all
            // top and bottom inventory
            if (event.getRawSlot() >= inventory.getSize()) {
                selectedSlot = slot;
                inventory.setItem(RC_Inventory.COLUMN_COUNT + 4, player.getInventory().getItem(slot));
                return;
            }
            // if accept
            if (slot == inventory.getSize() - 1) {
                accept = true;
                player.closeInventory();
                if (listener != null) {
                    listener.accept(player, selectedSlot);
                }
                return;
            }
            // if cancel
            if (slot == inventory.getSize() - RC_Inventory.COLUMN_COUNT) {
                player.closeInventory();
                return;
            }
        }

        @EventHandler
        public void close(InventoryCloseEvent event) {

            InventoryHolder holder = event.getInventory().getHolder();
            if (!(holder instanceof Player) || ((Player) holder) != player) {
                return;
            }
            HandlerList.unregisterAll(this);
            if (!accept && listener != null) {
                listener.cancel(player);
            }
        }
    }
}
