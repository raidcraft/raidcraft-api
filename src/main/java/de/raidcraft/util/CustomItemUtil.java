package de.raidcraft.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.ArmorType;
import de.raidcraft.api.items.CustomArmor;
import de.raidcraft.api.items.CustomEquipment;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.api.items.CustomItemManager;
import de.raidcraft.api.items.CustomItemStack;
import de.raidcraft.api.items.CustomWeapon;
import de.raidcraft.api.items.EquipmentSlot;
import de.raidcraft.api.items.tooltip.DurabilityTooltip;
import de.raidcraft.api.items.tooltip.EquipmentTypeTooltip;
import de.raidcraft.api.items.tooltip.FixedMultilineTooltip;
import de.raidcraft.api.items.tooltip.MetaDataTooltip;
import de.raidcraft.api.items.tooltip.SingleLineTooltip;
import de.raidcraft.api.items.tooltip.Tooltip;
import de.raidcraft.api.items.tooltip.TooltipSlot;
import de.raidcraft.api.items.tooltip.VariableMultilineTooltip;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Silthus
 */
public final class CustomItemUtil {

    public static final int ARMOR_SLOT = 1000;
    public static final String IGNORE_CODE = "&0&f";
    public static final int MAIN_WEAPON_SLOT = 0;
    public static final int OFFHAND_WEAPON_SLOT = 1;

    public static boolean isArmorSlot(int slot) {

        return slot >= ARMOR_SLOT && slot < ARMOR_SLOT + 4;
    }

    public static int getArmorSlot(ItemStack itemStack) {

        switch (itemStack.getType()) {

            case CHAINMAIL_HELMET:
            case DIAMOND_HELMET:
            case IRON_HELMET:
            case LEATHER_HELMET:
            case GOLD_HELMET:
                return 0;
            case CHAINMAIL_CHESTPLATE:
            case DIAMOND_CHESTPLATE:
            case IRON_CHESTPLATE:
            case LEATHER_CHESTPLATE:
            case GOLD_CHESTPLATE:
                return 1;
            case CHAINMAIL_LEGGINGS:
            case DIAMOND_LEGGINGS:
            case IRON_LEGGINGS:
            case LEATHER_LEGGINGS:
            case GOLD_LEGGINGS:
                return 2;
            case CHAINMAIL_BOOTS:
            case DIAMOND_BOOTS:
            case IRON_BOOTS:
            case LEATHER_BOOTS:
            case GOLD_BOOTS:
                return 3;
            default:
                return -1;
        }
    }

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

        if (itemMeta == null || !itemMeta.hasDisplayName()) {
            throw new CustomItemException("Item ist kein Custom Item.");
        }
        return decodeItemId(itemMeta.getDisplayName());
    }

    public static int decodeItemId(String str) throws CustomItemException {

        if (str.length() < 16) {
            throw new CustomItemException("Item contains an invalid item id. (String length < 16) -> " + str + "(" + str.length() + ")");
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            if (str.charAt(i) != ChatColor.COLOR_CHAR) {
                throw new CustomItemException("Item contains an invalid item id. (No color chars!) " + str);
            }
            i++;
            out.append(str.charAt(i));
        }
        return Integer.parseInt(out.toString(), 16);
    }

    public static int parseMetaDataId(ItemStack itemStack) {

        Map<TooltipSlot, Tooltip> tooltips = parseTooltips(itemStack);
        if (tooltips.containsKey(TooltipSlot.META_ID)) {
            return ((MetaDataTooltip) tooltips.get(TooltipSlot.META_ID)).getId();
        }
        return -1;
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

    public static boolean isOffhandWeapon(ItemStack itemStack) {

        return isWeapon(itemStack) && getWeapon(itemStack).getEquipmentSlot() == EquipmentSlot.SHIELD_HAND;
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

        for (int i = 9; i < inventory.length; i++) {
            if (inventory[i] == null) {
                return i;
            }
        }

        return -1;
    }

    public static boolean moveArmor(Player player, int slot, ItemStack item) {

        ItemStack[] armor = player.getInventory().getArmorContents();
        PlayerInventory inv = player.getInventory();
        int empty = firstEmpty(inv.getContents());
        if (empty == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            if (slot != -1) {
                armor[slot] = null;
                player.getInventory().setArmorContents(armor);
            }
            return false;
        }
        inv.setItem(empty, item);
        if (slot != -1) {
            armor[slot] = null;
            player.getInventory().setArmorContents(armor);
        }
        return true;
    }

    public static void denyItem(Player player, int slot, ItemStack itemStack, String message) {

        player.sendMessage(ChatColor.RED + message);
        if (CustomItemUtil.isArmorSlot(slot)) {
            CustomItemUtil.moveArmor(player, slot - CustomItemUtil.ARMOR_SLOT, itemStack);
        } else {
            CustomItemUtil.moveItem(player, slot, itemStack);
        }
    }

    public static boolean moveItem(Player player, int slot, ItemStack item) {

        PlayerInventory inv = player.getInventory();
        int empty = firstEmpty(inv.getContents());
        if (empty == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            if (slot != -1) {
                inv.setItem(slot, new ItemStack(Material.AIR));
            }
            return false;
        }
        inv.setItem(empty, item);
        if (slot != -1) {
            inv.setItem(slot, new ItemStack(Material.AIR));
        }
        return true;
    }

    public static int getPickupSlot(PlayerPickupItemEvent event) {

        // The event isn't canceled, this means this player can pick the item up.
        Inventory inventory = event.getPlayer().getInventory();

        ItemStack itemStack = event.getItem().getItemStack();
        int maxStackSize = itemStack.getMaxStackSize();

        int slotIndex = -1;
        for (int stackSize = 1; stackSize < maxStackSize; stackSize++) // Loop 1 less than max stack size so there is room for this item in the stack.
        {
            itemStack.setAmount(stackSize);
            slotIndex = inventory.first(itemStack);

            if (slotIndex != -1) {
                break;
            }
        }

        if (slotIndex == -1) {
            slotIndex = inventory.firstEmpty();
        }

        return slotIndex;
    }

    public static void setEquipmentTypeColor(Player player, ItemStack stack, ChatColor color) {

        if (stack == null || !isCustomItem(stack)) {
            return;
        }
        CustomItemStack itemStack = RaidCraft.getCustomItem(stack);
        if (itemStack == null || !itemStack.hasTooltip(TooltipSlot.EQUIPMENT_TYPE)) {
            return;
        }
        try {
            EquipmentTypeTooltip tooltip = (EquipmentTypeTooltip) itemStack.getTooltip(TooltipSlot.EQUIPMENT_TYPE);
            tooltip.setColor(color);
            itemStack.rebuild(player);
        } catch (CustomItemException ignored) {
        }
    }

    private static final Pattern DURABILITY_PATTERN = Pattern.compile("^Haltbarkeit: ([0-9]+)/([0-9]+)$");

    public static Map<TooltipSlot, Tooltip> parseTooltips(ItemStack itemStack) {

        Map<TooltipSlot, Tooltip> tooltips = new HashMap<>();
        if (!itemStack.hasItemMeta() || !itemStack.getItemMeta().hasLore()) {
            return tooltips;
        }
        List<String> lore = itemStack.getItemMeta().getLore();
        TooltipSlot slot = null;
        int multilineStart = -1;
        for (int i = 0; i < lore.size(); i++) {
            int tooltipSlotId = 0;
            try {
                tooltipSlotId = decodeItemId(lore.get(i));
            } catch (CustomItemException e) {
                e.printStackTrace();
                continue;
            }
            // remove the hidden line id
                lore.set(i, lore.get(i).substring(16));
                if (slot == TooltipSlot.values()[tooltipSlotId]) {
                    slot = TooltipSlot.values()[tooltipSlotId];
                    // we have a multiline tooltip
                    if (multilineStart < 0) multilineStart = i;
                } else if (multilineStart > -1) {
                    // the multiline tooltip ended in the last iteration
                    Tooltip tooltip;
                    List<String> strings = lore.subList(multilineStart, i);
                    switch (slot.getLineType()) {
                        case FIXED_MULTI_LINE:
                            tooltip = new FixedMultilineTooltip(slot, strings.toArray(new String[strings.size()]));
                            break;
                        default:
                        case VARIABLE_MULTI_LINE:
                            String text = String.join(" ", strings);
                            ChatColor color = ChatColor.GOLD;
                            boolean italic = false;
                            if (text.startsWith(ChatColor.COLOR_CHAR + "")) {
                                if (ChatColor.getByChar(text.charAt(1)) == ChatColor.ITALIC) {
                                    italic = true;
                                } else {
                                    color = ChatColor.getByChar(text.charAt(1));
                                }
                                text = text.substring(2);
                                if (text.startsWith(ChatColor.COLOR_CHAR + "") && ChatColor.getByChar(text.charAt(1)) == ChatColor.ITALIC) {
                                    italic = true;
                                    text = text.substring(2);
                                }
                            }
                            boolean quote = text.startsWith("\"") && text.endsWith("\"");
                            if (quote) text = text.substring(1, text.length() - 1);
                            tooltip = new VariableMultilineTooltip(slot, ChatColor.stripColor(text), quote, italic, color);
                            break;
                    }
                    tooltips.put(slot, tooltip);
                    multilineStart = -1;
                    // repeat the iteration to catch the current line
                    i--;
                } else {
                    Tooltip tooltip = null;
                    slot = TooltipSlot.values()[tooltipSlotId];
                    String line = lore.get(i);
                    switch (slot) {
                        case META_ID:
                            try {
                                tooltip = new MetaDataTooltip(decodeItemId(line));
                            } catch (CustomItemException e) {
                                e.printStackTrace();
                                continue;
                            }
                            break;
                        case MISC:
                            ChatColor color = ChatColor.WHITE;
                            if (line.startsWith(ChatColor.COLOR_CHAR + "")) {
                                color = ChatColor.getByChar(line.charAt(2));
                                line = line.substring(2);
                            }
                            tooltip = new SingleLineTooltip(slot, line, color);
                            break;
                        case DURABILITY:
                            Matcher matcher = DURABILITY_PATTERN.matcher(ChatColor.stripColor(line));
                            if (matcher.matches()) {
                                int durability = Integer.parseInt(matcher.group(1));
                                durability = durability < 1 ? 0 : durability;
                                int maxDurability = Integer.parseInt(matcher.group(2));
                                maxDurability = durability > maxDurability ? durability : maxDurability;
                                // set the new tooltip line
                                tooltip = new DurabilityTooltip(durability, maxDurability);
                            }
                            break;
                    }
                    if (tooltip != null) tooltips.put(slot, tooltip);
                }
        }
        return tooltips;
    }
}
