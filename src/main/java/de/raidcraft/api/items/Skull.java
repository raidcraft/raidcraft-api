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

        if (name.contains(":")) {
            name = name.split(":")[0];
        }

        if (name.equalsIgnoreCase("creeper")) {
            return new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.CREEPER.ordinal());
        }
        if (name.equalsIgnoreCase("skeleton")) {
            return new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.SKELETON.ordinal());
        }
        if (name.equalsIgnoreCase("wither")) {
            return new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.WITHER.ordinal());
        }
        if (name.equalsIgnoreCase("zombie")) {
            return new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.ZOMBIE.ordinal());
        }
        if (name.equalsIgnoreCase("enderman")) {
            return getPlayerSkull("Violit", "Enderman");
        }
        if (name.equalsIgnoreCase("blaze")) {
            return getPlayerSkull("Blaze_Head", "Blaze");
        }
        if (name.equalsIgnoreCase("spider")) {
            return getPlayerSkull("Kelevra_V", "Spider");
        }
        return getPlayerSkull(name, name);
    }

    public static ItemStack getPlayerSkull(String name) {

        return getPlayerSkull(name, name);
    }

    public static ItemStack getPlayerSkull(String name, String displayName) {

        ItemStack skullItem = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta skullMeta = (SkullMeta)skullItem.getItemMeta();
        skullMeta.setOwner(name);
        skullMeta.setDisplayName(ChatColor.RESET + displayName);
        skullItem.setItemMeta(skullMeta);
        return skullItem;
    }
}
