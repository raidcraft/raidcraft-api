package de.raidcraft.api.chestui;


import org.bukkit.entity.Player;

/**
 * @author Dragonfire
 */
public interface ItemSelectorListener {

    public void cancel(Player player);

    public void accept(Player player, int itemSlot);
}
