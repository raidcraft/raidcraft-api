package de.raidcraft.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.util.items.serialazition.BookSerialization;
import de.raidcraft.util.items.serialazition.EnchantmentSerialization;
import de.raidcraft.util.items.serialazition.FireworkEffectSerialization;
import de.raidcraft.util.items.serialazition.Serializable;
import org.apache.commons.lang.WordUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;

import java.util.Arrays;

/**
 * @author Silthus
 */
public final class ItemUtils {

    private ItemUtils() {

    }

    public static String toString(ItemStack itemStack) {

        if (CustomItemUtil.isCustomItem(itemStack)) {
            return itemStack.getAmount() + "x " + RaidCraft.getCustomItem(itemStack).getItem().toString();
        } else {
            return itemStack.getAmount() + "x " + RaidCraft.getItemIdString(itemStack, false);
        }
    }

    public static Material getItem(String name) {

        if (name == null || name.equals("")) {
            return null;
        }
        if (name.contains(":")) {
            name = name.split(":")[0];
        }
        try {
            return getItem(Integer.parseInt(name));
        } catch (NumberFormatException e) {
            return Material.matchMaterial(name);
        }
    }

    public static ItemStack getItemStackByString(String name) {

        String[] parts = name.split(":");
        Material material = getItem(parts[0]);
        short subid = 0;
        if (parts.length > 1) {
            try {
                subid = Short.valueOf(parts[1]);
            } catch (NumberFormatException ignored) {
            }
        }
        return new ItemStack(material, 1, subid);
    }

    public static Material getItem(int id) {

        return Material.getMaterial(id);
    }

    public static short getItemData(String item) {

        try {
            String[] split = item.split(":");
            return Short.parseShort(split[1]);
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getFriendlyName(String name) {

        return WordUtils.capitalize(name.replace("_", " ").toLowerCase());
    }

    public static String getFriendlyName(Material material) {

        return getFriendlyName(material.name());
    }

    public static String getFriendlyName(Material material, Language language) {

        if (Item.getItemByMaterial(material) != null) {
            return Item.getItemByMaterial(material).getFriendlyName(language);
        }
        return getFriendlyName(material);
    }

    public static String getFriendlyName(int id, Language language) {

        return getFriendlyName(Material.getMaterial(id), language);
    }

    public static String getFriendlyName(int id) {

        return getFriendlyName(Material.getMaterial(id));
    }

    public static boolean isStackValid(ItemStack item) {

        return item != null && item.getAmount() > 0 && item.getTypeId() > 0;
    }

    public enum Item {

        // tools
        WOODEN_SHOVEL(Material.WOOD_SPADE, ItemType.TOOL, "Holzschaufel"),
        STONE_SHOVEL(Material.STONE_SPADE, ItemType.TOOL, "Steinschaufel"),
        IRON_SHOVEL(Material.IRON_SPADE, ItemType.TOOL, "Eisenschaufel"),
        GOLD_SHOVEL(Material.GOLD_SPADE, ItemType.TOOL, "Goldschaufel"),
        DIAMOND_SHOVEL(Material.DIAMOND_SPADE, ItemType.TOOL, "Diamantschaufel"),

        WOODEN_PICKAXE(Material.WOOD_PICKAXE, ItemType.TOOL, "Holzspitzhacke"),
        STONE_PICKAXE(Material.STONE_PICKAXE, ItemType.TOOL, "Steinspitzhacke"),
        IRON_PICKAXE(Material.IRON_PICKAXE, ItemType.TOOL, "Eisenspitzhacke"),
        GOLD_PICKAXE(Material.GOLD_PICKAXE, ItemType.TOOL, "Goldspitzhacke"),
        DIAMOND_PICKAXE(Material.DIAMOND_PICKAXE, ItemType.TOOL, "Diamantspitzhacke"),

        // blocks
        LOG(Material.LOG, ItemType.BLOCK, "Holz"),
        STONE(Material.STONE, ItemType.BLOCK, "Stein"),
        DIRT(Material.DIRT, ItemType.BLOCK, "Erde"),
        GRAVEL(Material.GRAVEL, ItemType.BLOCK, "Kies"),
        GRASS(Material.GRASS, ItemType.BLOCK, "Gras"),
        SAND(Material.SAND, ItemType.BLOCK, "Sand"),
        CLAY(Material.CLAY, ItemType.BLOCK, "Lehm"),
        SMOOTH_BRICK(Material.SMOOTH_BRICK, ItemType.BLOCK, "Steinziegel"),
        COBBLESTONE(Material.COBBLESTONE, ItemType.BLOCK, "Pflasterstein"),
        LEAVES(Material.LEAVES, ItemType.BLOCK, "Blätter");

        private Material material;
        private ItemType type;
        private String germanFriendlyName;

        private Item(Material material, ItemType type, String germanFriendlyName) {

            this.material = material;
            this.type = type;
            this.germanFriendlyName = germanFriendlyName;
        }

        public Material getMaterial() {

            return material;
        }

        public ItemType getType() {

            return type;
        }

        public String getGermanFriendlyName() {

            return germanFriendlyName;
        }

        public String getEnglishFriendlyName() {

            return ItemUtils.getFriendlyName(material);
        }

        public String getFriendlyName(Language language) {

            if (language == Language.GERMAN) {
                return getGermanFriendlyName();
            }

            return getEnglishFriendlyName();
        }

        public static Item getItemByMaterial(Material material) {

            for (Item item : Item.values()) {
                if (item.getMaterial().equals(material)) {
                    return item;
                }
            }
            return null;
        }
    }

    public enum ItemType {
        TOOL,
        BLOCK
    }

    public enum Language {
        ENGLISH,
        GERMAN
    }

    @Deprecated
    public static class Serialization {

        private ItemStack item;
        private Serializable serializable;

        public Serialization(ItemStack item) {

            this.item = item;

            if (item.getType() == Material.FIREWORK) {
                serializable = new FireworkEffectSerialization(item);
            } else if (item.getType() == Material.BOOK_AND_QUILL) {
                serializable = new BookSerialization(item);
            } else {
                serializable = new EnchantmentSerialization(item);
            }
        }

        public String getSerializedItemData() {

            return serializable.serialize();
        }

        public ItemStack getDeserializedItem(String serializedData) {

            return serializable.deserialize(serializedData);
        }
    }

    public static ItemStack setDisplayName(ItemStack item, String name) {

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack getDye(DyeColor color) {

        Dye dye = new Dye();
        dye.setColor(color);
        return dye.toItemStack();
    }

    // TODO: use STAINED_GLASS_PANE
    public static ItemStack getGlassPane(DyeColor color, int amount) {

        return new ItemStack(160, amount, color.getWoolData());
    }

    public static ItemStack getGlassPane(DyeColor color, String name) {

        return setDisplayName(getGlassPane(color, 1), name);
    }

    public static ItemStack getGlassPane(DyeColor color) {

        return getGlassPane(color, 1);
    }

    public static ItemStack createItem(Material mat, String name) {

        return setDisplayName(new ItemStack(mat), name);
    }

    public static ItemStack setLore(ItemStack item, String... lore) {

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(Arrays.asList(lore));
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack clearLore(ItemStack item) {
        //TODO: use nms to remove build in lore (e.g. weapon value)
        return item;
    }
}
