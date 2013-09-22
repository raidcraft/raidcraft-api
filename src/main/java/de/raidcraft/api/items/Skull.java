package de.raidcraft.api.items;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Set;

/**
 * @author Philip
 */
public class Skull {

    public static boolean addHead(Player player, String skullOwner) {
        return addHead(player, skullOwner, 1);
    }

    public static boolean addHead(Player player, String skullOwner, int quantity) {
        PlayerInventory inv = player.getInventory();
        int firstEmpty = inv.firstEmpty();
        if (firstEmpty == -1) {
            return false;
        } else {
            inv.setItem(firstEmpty, getSkull(skullOwner, quantity));
            return true;
        }
    }

    public static String implode(Set<String> input, String glue) {
        int i = 0;
        StringBuilder output = new StringBuilder();
        for (String key : input) {
            if (i++ != 0) {
                output.append(glue);
            }
            output.append(key);
        }
        return output.toString();
    }

    public static String fixcase(String inputName) {
        String inputNameLC = inputName.toLowerCase();
        Player player = Bukkit.getServer().getPlayerExact(inputNameLC);

        if (player != null) {
            return player.getName();
        }

        for (OfflinePlayer offPlayer : Bukkit.getServer().getOfflinePlayers()) {
            if (offPlayer.getName().toLowerCase().equals(inputNameLC)) {
                return offPlayer.getName();
            }
        }

        return inputName;
    }

    public static ItemStack getSkull(String skullOwner) {

        if (skullOwner.contains(":")) {
            skullOwner = skullOwner.split(":")[1];
        }
        return getSkull(skullOwner, 1);
    }

    public static ItemStack getSkull(String skullOwner, int quantity) {
        String skullOwnerLC = skullOwner.toLowerCase();

        for (CustomSkullType skullType : CustomSkullType.values()) {
            if (skullOwnerLC.equals(skullType.getSpawnName().toLowerCase())) {
                return getSkull(skullType, quantity);
            }
        }

        switch (skullOwnerLC) {
            case "HEAD_SPAWN_CREEPER":
                return getSkull(SkullType.CREEPER, quantity);
            case "HEAD_SPAWN_ZOMBIE":
                return getSkull(SkullType.ZOMBIE, quantity);
            case "HEAD_SPAWN_SKELETON":
                return getSkull(SkullType.SKELETON, quantity);
            case "HEAD_SPAWN_WITHER":
                return getSkull(SkullType.WITHER, quantity);
            default:
                return getSkull(skullOwner, null, quantity);
        }
    }

    public static ItemStack getSkull(String skullOwner, String displayName) {
        return getSkull(skullOwner, displayName, 1);
    }

    public static ItemStack getSkull(String skullOwner, String displayName, int quantity) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, quantity, (short) SkullType.PLAYER.ordinal());
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwner(skullOwner);
        if (displayName != null) {
            skullMeta.setDisplayName(ChatColor.RESET + displayName);
        }
        skull.setItemMeta(skullMeta);
        return skull;
    }

    public static ItemStack getSkull(CustomSkullType type) {
        return getSkull(type, 1);
    }

    public static ItemStack getSkull(CustomSkullType type, int quantity) {
        return getSkull(type.getOwner(), type.getDisplayName(), quantity);
    }

    public static ItemStack getSkull(SkullType type) {
        return getSkull(type, 1);
    }

    public static ItemStack getSkull(SkullType type, int quantity) {
        return new ItemStack(Material.SKULL_ITEM, quantity, (short) type.ordinal());
    }

    public static String format(String text, String... replacement) {
        String output = text;
        for (int i = 0; i < replacement.length; i++) {
            output = output.replace("%" + (i + 1) + "%", replacement[i]);
        }
        return ChatColor.translateAlternateColorCodes('&', output);
    }

    public static void formatMsg(CommandSender player, String text, String... replacement) {
        player.sendMessage(format(text, replacement));
    }

    public static String formatStrip(String text, String... replacement) {
        return ChatColor.stripColor(format(text, replacement));
    }
}
