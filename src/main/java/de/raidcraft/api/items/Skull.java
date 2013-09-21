package de.raidcraft.api.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * @author Philip
 */
public class Skull {

    public static ItemStack getSkull(String name) {

        ItemStack skullItem = null;

        if(name.equalsIgnoreCase("creeper")) {
            skullItem = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.CREEPER.ordinal());
        }
        if(name.equalsIgnoreCase("skeleton")) {
            skullItem = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.SKELETON.ordinal());
        }
        if(name.equalsIgnoreCase("wither")) {
            skullItem = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.WITHER.ordinal());
        }
        if(name.equalsIgnoreCase("zombie")) {
            skullItem = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.ZOMBIE.ordinal());
        }

        if(name.equalsIgnoreCase("enderman")) {
            skullItem = getPlayerSkull("Violit", "Enderman");
        }

        if(name.equalsIgnoreCase("blaze")) {
            skullItem = getPlayerSkull("Blaze_Head", "Blaze");
        }

        if(name.equalsIgnoreCase("spider")) {
            skullItem = getPlayerSkull("Kelevra_V", "Spider");
        }

        if(skullItem == null) {
            skullItem = getPlayerSkull(name, name);
        }

        return skullItem;
    }

    private static ItemStack getPlayerSkull(String name, String displayName) {
        ItemStack skullItem = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta skullMeta = (SkullMeta)skullItem.getItemMeta();
        skullMeta.setOwner(name);
        skullMeta.setDisplayName(ChatColor.RESET + displayName);
        skullItem.setItemMeta(skullMeta);
        return skullItem;
    }
}
