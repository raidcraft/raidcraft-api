package de.raidcraft.api.chestui.menuitems;

import de.raidcraft.api.items.RcItems;
import org.bukkit.DyeColor;

/**
 * @author Sebastian
 */
public abstract class MenuPlus extends MenuItemAPI {

    public MenuPlus(String name) {

        setItem(RcItems.setDisplayName(RcItems.createDye(DyeColor.LIME), name));
    }
}
