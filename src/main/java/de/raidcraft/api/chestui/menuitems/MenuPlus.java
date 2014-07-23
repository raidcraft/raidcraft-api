package de.raidcraft.api.chestui.menuitems;

import de.raidcraft.api.chestui.MenuItemAPI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;

/**
 * @author Sebastian
 */
public abstract class MenuPlus implements MenuItemAPI {
    @Getter
    @Setter
    private ItemStack item;
    public MenuPlus(String name) {
        Dye data = new Dye();
        data.setColor(DyeColor.LIME);

        item = data.toItemStack();

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
    }
}
