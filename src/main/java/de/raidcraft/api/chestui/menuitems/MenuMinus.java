package de.raidcraft.api.chestui.menuitems;

import de.raidcraft.api.items.RcItems;
import org.bukkit.DyeColor;

/**
 * @author Sebastian
 */
public abstract class MenuMinus extends MenuItemAPI {

    public MenuMinus(String name) {

        setItem(RcItems.setDisplayName(RcItems.createDye(DyeColor.MAGENTA), name));
    }
}
