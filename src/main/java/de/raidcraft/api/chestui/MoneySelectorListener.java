package de.raidcraft.api.chestui;

import org.bukkit.entity.Player;

/**
 * @author Dragonfire
 */
public interface MoneySelectorListener {
    public void cancel(Player player);

    public void accept(Player player, double money);
}
