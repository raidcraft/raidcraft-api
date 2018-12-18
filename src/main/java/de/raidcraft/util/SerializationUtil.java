package de.raidcraft.util;

import de.raidcraft.util.items.serialazition.StaticFireworkEffectSerialization;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Silthus
 */
public class SerializationUtil {

    /**
     * Serializes a list of {@link ConfigurationSerializable}. This could be ItemMeta or ItemStacks, etc.
     *
     * @param list of {@link ConfigurationSerializable}
     *
     * @return list of serialized maps
     */
    public static List<Map<String, Object>> serializeItemList(List<ConfigurationSerializable> list) {

        List<Map<String, Object>> returnVal = new ArrayList<>();
        for (ConfigurationSerializable cs : list) {
            returnVal.add(serialize(cs));
        }
        return returnVal;
    }

    /**
     * Serializes one {@link ConfigurationSerializable} which could be an ItemMeta or ItemStack, etc.
     *
     * @param serializable that should be serialzed
     *
     * @return map containing all serialized objects
     */
    public static Map<String, Object> serialize(ConfigurationSerializable serializable) {

        Map<String, Object> serialized = recreateMap(serializable.serialize());
        for (Map.Entry<String, Object> entry : serialized.entrySet()) {
            if (entry.getValue() instanceof ConfigurationSerializable) {
                entry.setValue(serialize((ConfigurationSerializable) entry.getValue()));
            }
        }
        serialized.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(serializable.getClass()));
        return serialized;
    }

    /**
     * Deserialzes an map into a list of {@link ConfigurationSerializable} objects.
     *
     * @param itemList to deserialze
     *
     * @return list of {@link ConfigurationSerializable}
     */
    public static List<ConfigurationSerializable> deserializeItemList(List<Map<String, Object>> itemList) {

        List<ConfigurationSerializable> returnVal = new ArrayList<>();
        for (Map<String, Object> map : itemList) {
            returnVal.add(deserialize(map));
        }
        return returnVal;
    }

    /**
     * Clones an existing serialized map. This is needed because of the immutable return types.
     *
     * @param original map to clone
     *
     * @return cloned map
     */
    public static Map<String, Object> recreateMap(Map<String, Object> original) {

        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : original.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    /**
     * Deserializes a serialized map into a {@link ConfigurationSerializable} object.
     *
     * @param map to deserialize
     *
     * @return deserialized {@link ConfigurationSerializable}
     */
    @SuppressWarnings("unchecked")
    public static ConfigurationSerializable deserialize(Map<String, Object> map) {

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            // Check if any of its sub-maps are ConfigurationSerializable.  They need to be done first.
            if (entry.getValue() instanceof Map && ((Map) entry.getValue()).containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
                entry.setValue(deserialize((Map) entry.getValue()));
            }
        }
        return ConfigurationSerialization.deserializeObject(map);
    }

    public static String toByteStream(Map<String, Object> map) {

        // create streams
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // write map
            oos.writeObject(map);
            oos.flush();

            // toHexString
            return bytesToHex(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Serializes a {@link ConfigurationSerializable} into a savable byte stream for usage in databases and so on.
     *
     * @param serializable object that should be serialized
     *
     * @return string of bytes containing the serialized map of the input
     */
    public static String toByteStream(ConfigurationSerializable serializable) {

        return toByteStream(serialize(serializable));
    }

    public static String toByteStream(ItemMeta meta) {

        // workaround for not serializable meta
        if (meta instanceof FireworkMeta) {
            return StaticFireworkEffectSerialization.serialize((FireworkMeta) meta);
        }

        return toByteStream((ConfigurationSerializable) meta);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> mapFromByteStream(String hex) {

        // create streams
        byte[] bytes = hexStringToByteArray(hex);

        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (Map<String, Object>) ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Deserializes an previously serialized byte string into its original {@link ConfigurationSerializable} object.
     *
     * @param hex string serialized with {@link SerializationUtil#toByteStream(org.bukkit.configuration.serialization.ConfigurationSerializable)}
     *
     * @return deserialized object
     */
    @SuppressWarnings("unchecked")
    public static ConfigurationSerializable fromByteStream(String hex) {

        return deserialize(mapFromByteStream(hex));
    }

    public static ConfigurationSerializable fromByteStream(String hex, Material type) {

        if (type == Material.FIREWORK_ROCKET) {
            return StaticFireworkEffectSerialization.deserialize(hex);
        }

        return fromByteStream(hex);
    }

    /**
     * Converts the player inventory to a String array of Base64 strings. First string is the content and second string is the armor.
     *
     * @param playerInventory to turn into an array of strings.
     * @return Array of strings: [ main content, armor content ]
     * @throws IllegalStateException
     */
    public static String[] playerInventoryToBase64(PlayerInventory playerInventory) throws IllegalStateException {
        //get the main content part, this doesn't return the armor
        String content = inventoryToBase64(playerInventory);
        String armor = itemStackArrayToBase64(playerInventory.getArmorContents());

        return new String[] { content, armor };
    }

    /**
     * Converts a serialized player inventory back into its original form. First string is the content and second string is the armor.
     *
     * @param data Array of strings: [ main content, armor content ]
     * @return deserialized {@link PlayerInventory}
     * @throws IllegalStateException
     */
    public static PlayerInventory playerInventoryFromBase64(String[] data) throws IllegalStateException {

        try {
            Inventory content = inventoryFromBase64(data[0]);
            ItemStack[] armor = itemStackArrayFromBase64(data[1]);
            PlayerInventory inventory = (PlayerInventory) Bukkit.getServer().createInventory(null, InventoryType.PLAYER);
            inventory.setContents(content.getContents());
            inventory.setArmorContents(armor);
            return inventory;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to deserialize player inventory.", e);
        }
    }

    /**
     *
     * A method to serialize an {@link ItemStack} array to Base64 String.
     *
     * <p />
     *
     * Based off of {@link #inventoryToBase64(Inventory)}.
     *
     * @param items to turn into a Base64 String.
     * @return Base64 string of the items.
     * @throws IllegalStateException
     */
    public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(items.length);

            // Save every element in the list
            for (int i = 0; i < items.length; i++) {
                dataOutput.writeObject(items[i]);
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    /**
     * A method to serialize an inventory to Base64 string.
     *
     * <p />
     *
     * Special thanks to Comphenix in the Bukkit forums or also known
     * as aadnk on GitHub.
     *
     * <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
     *
     * @param inventory to serialize
     * @return Base64 string of the provided inventory
     * @throws IllegalStateException
     */
    public static String inventoryToBase64(Inventory inventory) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeObject(inventory.getType());

            // Save every element in the list
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    /**
     *
     * A method to get an {@link Inventory} from an encoded, Base64, string.
     *
     * <p />
     *
     * Special thanks to Comphenix in the Bukkit forums or also known
     * as aadnk on GitHub.
     *
     * <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
     *
     * @param data Base64 string of data containing an inventory.
     * @return Inventory created from the Base64 string.
     * @throws IOException
     */
    public static Inventory inventoryFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = Bukkit.getServer().createInventory(null, (InventoryType) dataInput.readObject());

            // Read the serialized inventory
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }

            dataInput.close();
            return inventory;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    /**
     * Gets an array of ItemStacks from Base64 string.
     *
     * <p />
     *
     * Base off of {@link #inventoryFromBase64(String)}.
     *
     * @param data Base64 string to convert to ItemStack array.
     * @return ItemStack array created from the Base64 string.
     * @throws IOException
     */
    public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}
