package de.raidcraft.api.items;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;

import java.util.Arrays;

/**
 * @author Sebastian
 */
public class RC_Items {

    public static ItemStack createDye(DyeColor color) {

        Dye dye = new Dye();
        dye.setColor(color);
        return dye.toItemStack();
    }

    // TODO: use STAINED_GLASS_PANE
    public static ItemStack getGlassPane(DyeColor color, int amount) {

        return new ItemStack(160, amount, color.getWoolData());
    }

    public static ItemStack getGlassPane(DyeColor color) {

        return getGlassPane(color, 1);
    }



    public static ItemStack createItem(Material mat, String name) {

        return setDisplayName(new ItemStack(mat), name);
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
