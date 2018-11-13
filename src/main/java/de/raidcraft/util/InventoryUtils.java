package de.raidcraft.util;


import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItemStack;
import de.raidcraft.api.quests.QuestProvider;
import de.raidcraft.api.quests.Quests;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Dragonfire
 */
public class InventoryUtils {
    public final static int COLUMN_COUNT = 9;
    public final static int MAX_ROWS = 6;

    /**
     * Tries to add the given {@link ItemStack} to the players inventory.
     * If the inventory is full every available space is filled and the rest is dropped in front of the {@link Player}.
     *
     * @param player to add items to
     * @param items  to add
     */
    public static void addOrDropItems(Player player, ItemStack... items) {

        if (items == null) return;

        Optional<QuestProvider> questProvider = Quests.getQuestProvider();
        if (questProvider.isPresent()) {
            Arrays.stream(items).filter(Objects::nonNull)
                    .filter(RaidCraft::isCustomItem)
                    .map(RaidCraft::getCustomItem)
                    .filter(CustomItemStack::isQuestItem)
                    .forEach(itemStack -> questProvider.get().addQuestItem(player, itemStack));

            items = Arrays.stream(items).filter(Objects::nonNull)
                    .filter(itemStack -> !RaidCraft.isCustomItem(itemStack) || !(RaidCraft.getCustomItem(itemStack).isQuestItem()))
                    .toArray(ItemStack[]::new);
        }

        HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(Arrays.stream(items).filter(Objects::nonNull).toArray(ItemStack[]::new));
        leftovers.values().forEach(item -> player.getWorld().dropItem(player.getLocation(), item));
    }

    /**
     * Sets the given {@link ItemStack} in the @{@link Player} {@link org.bukkit.inventory.Inventory}
     * at the given {@param slot}. If there is an existing item at the slot it will add it the Inventory
     * using {@link #addOrDropItems(Player, ItemStack...)}.
     *
     * @param player to set the item stack to
     * @param item   to set
     * @param slot   to set at
     */
    public static void setAndDropOrAddItem(Player player, ItemStack item, int slot) {

        if (item == null) return;
        ItemStack previousItem = player.getInventory().getItem(slot);
        player.getInventory().setItem(slot, item);
        if (previousItem != null) addOrDropItems(player, previousItem);
    }
}
