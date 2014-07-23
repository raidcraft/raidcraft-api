package de.raidcraft.api.items;

import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;

import java.util.Arrays;

/**
 * @author Sebastian
 */
public class RcItems {

    public static ItemStack createDye(DyeColor color) {

        Dye dye = new Dye();
        dye.setColor(color);
        return dye.toItemStack();
    }

    // TODO: use STAINED_GLASS_PANE
    public static ItemStack getGlassPane(DyeColor color) {

        return new ItemStack(160, 1, color.getWoolData());
    }

    public static ItemStack setLore(ItemStack item, String... lore){
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(Arrays.asList(lore));
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack setDisplayName(ItemStack item, String name){
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);
        return item;
    }

}
