package de.raidcraft.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.ArmorType;
import de.raidcraft.api.items.CustomArmor;
import de.raidcraft.api.items.CustomEquipment;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.api.items.CustomItemManager;
import de.raidcraft.api.items.CustomItemStack;
import de.raidcraft.api.items.CustomWeapon;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Silthus
 */
public final class CustomItemUtil {

    public static String encodeItemId(int id) {

        String hex = String.format("%08x", id);
        StringBuilder out = new StringBuilder();
        for (char h : hex.toCharArray()) {
            out.append(ChatColor.COLOR_CHAR);
            out.append(h);
        }
        return out.toString();
    }

    public static boolean isCustomItem(ItemStack itemStack) {

        if (itemStack == null) return false;
        try {
            decodeItemId(itemStack.getItemMeta());
            return true;
        } catch (CustomItemException ignored) {
        }
        return false;
    }

    public static boolean isStackableCustomItem(ItemStack itemStack) {

        // in our custom item stack we override the getMaxStackSize method
        return isCustomItem(itemStack) && itemStack.getType().getMaxStackSize() != RaidCraft.getCustomItem(itemStack).getMaxStackSize();
    }

    public static boolean isExceedMaxStackableCustomItem(ItemStack itemStack) {

        return isStackableCustomItem(itemStack) && itemStack.getAmount() > itemStack.getMaxStackSize();
    }

    public static int decodeItemId(ItemMeta itemMeta) throws CustomItemException {

        if (itemMeta == null || !itemMeta.hasDisplayName() || !itemMeta.hasLore()) {
            throw new CustomItemException("Item ist kein Custom Item.");
        }
        return decodeItemId(itemMeta.getDisplayName());
    }

    public static int decodeItemId(String str) throws CustomItemException {

        if (str.length() < 16) {
            throw new CustomItemException("Item ist kein Custom Item.");
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            if (str.charAt(i) != ChatColor.COLOR_CHAR)
                throw new CustomItemException("Item ist kein Custom Item.");
            i++;
            out.append(str.charAt(i));
        }
        return Integer.parseInt(out.toString(), 16);
    }

    public static int getStringWidth(String str) {

        str = ChatColor.stripColor(str);
        int width = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            width += Font.WIDTHS[c] + 1;
        }
        return width;
    }

    public static int getStringWidthBold(String str) {

        str = ChatColor.stripColor(str);
        int width = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            width += Font.WIDTHS[c] + 2;
        }
        return width;
    }

    public static int checkWidth(String str, int width) {

        return checkWidth(str, width, false);
    }

    public static int checkWidth(String str, int width, boolean bold) {

        if (bold) {
            int dWidth = getStringWidthBold(str);
            if (dWidth > width) return dWidth;
            return width;
        } else {
            int dWidth = getStringWidth(str);
            if (dWidth > width) return dWidth;
            return width;
        }
    }

    public static String getSellPriceString(double price) {

        return getSellPriceString(price, ChatColor.WHITE);
    }

    public static String getSellPriceString(double price, ChatColor textColor) {

        StringBuilder sb = new StringBuilder();
        if (price < 0) {
            sb.append(ChatColor.RED).append('-');
            price = Math.abs(price);
        }
        int gold = (int) price / 100;
        sb.append(textColor).append(gold).append(ChatColor.GOLD).append('●');
        int silver = (int) price % 100;
        sb.append(textColor);
        if (silver < 10) {
            sb.append("0");
        }
        sb.append(silver).append(ChatColor.GRAY).append('●');
        int copper = (int) (price * 100) % 100;
        sb.append(textColor);
        if (copper < 10) {
            sb.append("0");
        }
        sb.append(copper).append(ChatColor.RED).append('●');
        return sb.toString();
    }

    public static String getSwingTimeString(double time) {

        time = (int) (time * 100) / 100.0;
        return Double.toString(time);
    }

    public static boolean isEquipment(ItemStack itemStack) {

        if (itemStack == null || itemStack.getTypeId() == 0) {
            return false;
        }
        CustomItemStack customItem = RaidCraft.getComponent(CustomItemManager.class).getCustomItem(itemStack);
        return customItem != null && customItem.getItem() instanceof CustomEquipment;
    }

    public static boolean isWeapon(ItemStack itemStack) {

        if (itemStack == null || itemStack.getTypeId() == 0) {
            return false;
        }
        CustomItemStack customItem = RaidCraft.getComponent(CustomItemManager.class).getCustomItem(itemStack);
        return customItem != null && customItem.getItem() instanceof CustomWeapon;
    }

    public static CustomWeapon getWeapon(ItemStack itemStack) {

        CustomItemStack customItem = RaidCraft.getComponent(CustomItemManager.class).getCustomItem(itemStack);
        if (customItem != null && customItem.getItem() instanceof CustomWeapon) {
            return (CustomWeapon) customItem.getItem();
        }
        return null;
    }

    public static CustomArmor getArmor(ItemStack itemStack) {

        CustomItemStack customItem = RaidCraft.getComponent(CustomItemManager.class).getCustomItem(itemStack);
        if (customItem != null && customItem.getItem() instanceof CustomArmor) {
            return (CustomArmor) customItem.getItem();
        }
        return null;
    }

    public static boolean isShield(ItemStack itemStack) {

        if (itemStack == null || itemStack.getTypeId() == 0) {
            return false;
        }
        CustomItemStack customItem = RaidCraft.getComponent(CustomItemManager.class).getCustomItem(itemStack);
        return customItem != null
                && customItem.getItem() instanceof CustomArmor
                && ((CustomArmor) customItem.getItem()).getArmorType() == ArmorType.SHIELD;
    }

    public static boolean isArmor(ItemStack itemStack) {

        if (itemStack == null || itemStack.getTypeId() == 0) {
            return false;
        }
        CustomItemStack customItem = RaidCraft.getComponent(CustomItemManager.class).getCustomItem(itemStack);
        return customItem != null
                && customItem.getItem() instanceof CustomArmor
                && ((CustomArmor) customItem.getItem()).getArmorType() != ArmorType.SHIELD;
    }

    public static short getMinecraftDurability(ItemStack itemStack, int durability, int maxDurability) {

        double durabilityInPercent = (double) durability / (double) maxDurability;
        // also set the minecraft items durability
        // minecrafts max durability is when the item is completly broken so we need to invert our durability
        double mcDurabilityPercent = 1.0 - durabilityInPercent;
        // always set -1 so we dont break the item
        return (short) ((itemStack.getType().getMaxDurability() * mcDurabilityPercent) - 1);
    }

    public static boolean isEqualCustomItem(ItemStack stack1, ItemStack stack2) {

        if (stack1 == null || stack2 == null) {
            return false;
        }
        CustomItemStack item1 = RaidCraft.getCustomItem(stack1);
        CustomItemStack item2 = RaidCraft.getCustomItem(stack2);

        if (item1 == null && item2 == null) {
            return stack1.isSimilar(stack2);
        }

        return !(item1 == null || item2 == null) && item1.equals(item2);
    }

    public static int firstEmpty(ItemStack... inventory) {

        for (int i = 9; i < inventory.length; i++)
            if (inventory[i] == null)
                return i;

        return -1;
    }

    public static boolean moveItem(Player player, int slot, ItemStack item) {

        PlayerInventory inv = player.getInventory();
        int empty = firstEmpty(inv.getContents());
        if (empty == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            if (slot != -1) {
                inv.clear(slot);
            }
            return false;
        }
        inv.setItem(empty, item);
        if (slot != -1) {
            inv.clear(slot);
        }
        return true;
    }
}
