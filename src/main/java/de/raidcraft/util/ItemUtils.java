package de.raidcraft.util;

import de.raidcraft.util.items.serialazition.BookSerialization;
import de.raidcraft.util.items.serialazition.EnchantmentSerialization;
import de.raidcraft.util.items.serialazition.FireworkEffectSerialization;
import de.raidcraft.util.items.serialazition.Serializable;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author Silthus
 */
public final class ItemUtils {

    private ItemUtils() {

    }

    public static Material getItem(String name) {

        if (name == null || name.equals("")) {
            return null;
        }
        try {
            return getItem(Integer.parseInt(name));
        } catch (NumberFormatException e) {
            return Material.getMaterial(name.toUpperCase());
        }
    }

    public static ItemStack getItemStackByString(String name) {

        String[] parts = name.split(":");
        Material material = getItem(parts[0]);
        short subid = 0;
        if(parts.length > 1) {
            try {
                subid = Short.valueOf(parts[1]);
            }
            catch(NumberFormatException e) {}
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
            return -1;
        }
    }

    public static String getFriendlyName(String name) {

        return WordUtils.capitalize(name.replace("_", " ").toLowerCase());
    }

    public static String getFriendlyName(Material material) {

        return getFriendlyName(material.name());
    }

    public static String getFriendlyName(Material material, Language language) {

        if(Item.getItemByMaterial(material) != null) {
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
        DIAMOND_PICKAXE(Material.DIAMOND_PICKAXE, ItemType.TOOL, "Diamantspitzhacke");


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

            if(language == Language.GERMAN) {
                return getGermanFriendlyName();
            }

            return getEnglishFriendlyName();
        }

        public static Item getItemByMaterial(Material material) {
            for(Item item : Item.values()) {
                if(item.getMaterial().equals(material)) {
                    return item;
                }
            }
            return null;
        }
    }

    public enum ItemType {
        TOOL
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

            if(item.getType() == Material.FIREWORK) {
                serializable = new FireworkEffectSerialization(item);
            }
            else if(item.getType() == Material.BOOK_AND_QUILL) {
                serializable = new BookSerialization(item);
            }
            else {
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
}
