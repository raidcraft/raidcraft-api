package de.raidcraft.util;

import de.raidcraft.util.items.serialazition.BookSerialization;
import de.raidcraft.util.items.serialazition.EnchantmentSerialization;
import de.raidcraft.util.items.serialazition.FireworkEffectSerialization;
import de.raidcraft.util.items.serialazition.Serializable;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * Serializes the MetaData of an ItemStack
     * and marshals it then in a String
     * @param itemMeta The item to serialize
     * @return The marshaled ItemStack as String
     * @throws IOException On any internal Exception
     */
    public static String serializeItemMeta(ItemMeta itemMeta) throws IOException {

        // create streams
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        // write map
        Map<String, Object> serialize = new HashMap<>(itemMeta.serialize());
        serialize.put("==", serialize.get("meta-type"));
        oos.writeObject(serialize);
        oos.flush();

        // toHexString
        return new HexBinaryAdapter().marshal(baos.toByteArray());
    }

    /**
     * Deserializes the ItemMeta of an ItemStack from a marshaled String
     * @param hex ItemMeta marshaled in a String
     * @return The deserialized ItemMeta
     * @throws IOException On any internal exception
     */
    @SuppressWarnings("unchecked")
    public static ItemMeta deserializeItemMeta(String hex) throws IOException {

        // create streams
        byte[] bytes = new HexBinaryAdapter().unmarshal(hex);

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);

        Map<String, Object> objectMap = new HashMap<>();
        try {
            objectMap = (Map<String, Object>) ois.readObject();
        } catch (ClassNotFoundException cnfe) {
            throw new IOException(cnfe);
        } catch (EOFException ignored) {
        }
        return (ItemMeta) ConfigurationSerialization.deserializeObject(objectMap);
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
