package de.raidcraft.api.chestui;


import org.bukkit.entity.Player;

/**
 * @author Dragonfire
 */
public interface ItemSelectorListener {

    void cancel(Player player);

    void accept(Player player, int itemSlot);
}
