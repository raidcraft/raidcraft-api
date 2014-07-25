package de.raidcraft.api.chestui.menuitems;

import org.bukkit.entity.Player;

/**
 * A Menu Item without any function if a player click it.
 * Overwrite #trigger method(Player) to implement an reaction.
 *
 * @author Dragonfire
 */
public class MenuItem extends MenuItemAPI {

    @Override
    public void trigger(Player player) {
        // nothing
    }
}
