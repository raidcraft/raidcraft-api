package de.raidcraft.util;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
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
import java.util.HashSet;
import java.util.Set;

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

        if (Strings.isNullOrEmpty(name)) {
            return null;
        }
        return Material.matchMaterial(name);
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

    public static boolean isStackValid(ItemStack item) {

        return item != null && item.getAmount() > 0;
    }

    public enum Item {

        // tools
        WOODEN_SHOVEL(Material.WOODEN_SHOVEL, ItemType.TOOL, "Holzschaufel"),
        STONE_SHOVEL(Material.STONE_SHOVEL, ItemType.TOOL, "Steinschaufel"),
        IRON_SHOVEL(Material.IRON_SHOVEL, ItemType.TOOL, "Eisenschaufel"),
        GOLD_SHOVEL(Material.GOLDEN_SHOVEL, ItemType.TOOL, "Goldschaufel"),
        DIAMOND_SHOVEL(Material.DIAMOND_SHOVEL, ItemType.TOOL, "Diamantschaufel"),

        WOODEN_PICKAXE(Material.WOODEN_PICKAXE, ItemType.TOOL, "Holzspitzhacke"),
        STONE_PICKAXE(Material.STONE_PICKAXE, ItemType.TOOL, "Steinspitzhacke"),
        IRON_PICKAXE(Material.IRON_PICKAXE, ItemType.TOOL, "Eisenspitzhacke"),
        GOLD_PICKAXE(Material.GOLDEN_PICKAXE, ItemType.TOOL, "Goldspitzhacke"),
        DIAMOND_PICKAXE(Material.DIAMOND_PICKAXE, ItemType.TOOL, "Diamantspitzhacke"),

        WOODEN_HOE(Material.WOODEN_HOE, ItemType.TOOL, "Holzhacke"),
        STONE_HOE(Material.STONE_HOE, ItemType.TOOL, "Steinhacke"),
        IRON_HOE(Material.IRON_HOE, ItemType.TOOL, "Eisenhacke"),
        GOLD_HOE(Material.GOLDEN_HOE, ItemType.TOOL, "Goldhacke"),
        DIAMOND_HOE(Material.DIAMOND_HOE, ItemType.TOOL, "Diamanthacke"),

        // blocks
        LOG(Material.ACACIA_LOG, ItemType.BLOCK, "Holz"),
        STONE(Material.STONE, ItemType.BLOCK, "Stein"),
        DIRT(Material.DIRT, ItemType.BLOCK, "Erde"),
        GRAVEL(Material.GRAVEL, ItemType.BLOCK, "Kies"),
        GRASS(Material.GRASS, ItemType.BLOCK, "Gras"),
        SAND(Material.SAND, ItemType.BLOCK, "Sand"),
        CLAY(Material.CLAY, ItemType.BLOCK, "Lehm"),
        SMOOTH_BRICK(Material.SMOOTH_STONE, ItemType.BLOCK, "Steinziegel"),
        COBBLESTONE(Material.COBBLESTONE, ItemType.BLOCK, "Pflasterstein"),
        LEAVES(Material.ACACIA_LEAVES, ItemType.BLOCK, "Blätter");

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

            if (item.getType() == Material.FIREWORK_ROCKET) {
                serializable = new FireworkEffectSerialization(item);
            } else if (item.getType() == Material.WRITTEN_BOOK) {
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
        return dye.toItemStack(1);
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

    public static final Set<Material> LOGS = Sets.newHashSet(
            Material.ACACIA_LOG,
            Material.OAK_LOG,
            Material.BIRCH_LOG,
            Material.DARK_OAK_LOG,
            Material.JUNGLE_LOG,
            Material.SPRUCE_LOG,
            Material.STRIPPED_ACACIA_LOG,
            Material.STRIPPED_BIRCH_LOG,
            Material.STRIPPED_DARK_OAK_LOG,
            Material.STRIPPED_JUNGLE_LOG,
            Material.STRIPPED_OAK_LOG,
            Material.STRIPPED_SPRUCE_LOG
    );

    public static boolean isLog(Material material) {
        return LOGS.contains(material);
    }

    public static final Set<Material> LEAVES = Sets.newHashSet(
            Material.ACACIA_LEAVES,
            Material.BIRCH_LEAVES,
            Material.DARK_OAK_LEAVES,
            Material.JUNGLE_LEAVES,
            Material.OAK_LEAVES,
            Material.SPRUCE_LEAVES
    );

    public static boolean isLeaves(Material material) {
        return LEAVES.contains(material);
    }

    public static final Set<Material> DOORS = Sets.newHashSet(
            Material.DARK_OAK_DOOR,
            Material.ACACIA_DOOR,
            Material.BIRCH_DOOR,
            Material.IRON_DOOR,
            Material.JUNGLE_DOOR,
            Material.OAK_DOOR,
            Material.SPRUCE_DOOR
    );

    public static boolean isDoor(Material material) {
        return DOORS.contains(material);
    }

    public static Set<Material> WOOLS = Sets.newHashSet(
            Material.WHITE_WOOL,
            Material.BLACK_WOOL,
            Material.BLUE_WOOL,
            Material.BROWN_WOOL,
            Material.CYAN_WOOL,
            Material.GRAY_WOOL,
            Material.GREEN_WOOL,
            Material.LIGHT_BLUE_WOOL,
            Material.LIGHT_GRAY_WOOL,
            Material.LIME_WOOL,
            Material.MAGENTA_WOOL,
            Material.ORANGE_WOOL,
            Material.PINK_WOOL,
            Material.PURPLE_WOOL,
            Material.RED_WOOL,
            Material.YELLOW_WOOL
    );

    public static boolean isWool(Material material) {
        return WOOLS.contains(material);
    }
}
