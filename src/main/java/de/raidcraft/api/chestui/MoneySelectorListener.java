package de.raidcraft.api.chestui;

import org.bukkit.entity.Player;

/**
 * @author Dragonfire
 */
public interface MoneySelectorListener {
    void cancel(Player player);

    void accept(Player player, double money);
}
