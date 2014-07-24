package de.raidcraft.api.chestui.menuitems;

import de.raidcraft.api.items.RC_Items;
import org.bukkit.DyeColor;

/**
 * @author Sebastian
 */
public abstract class MenuMinus extends MenuItemAPI {

    public MenuMinus(String name) {

        setItem(RC_Items.setDisplayName(RC_Items.createDye(DyeColor.MAGENTA), name));
    }
}
