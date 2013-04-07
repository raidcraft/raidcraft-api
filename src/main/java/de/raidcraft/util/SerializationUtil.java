package de.raidcraft.util;

import de.raidcraft.util.items.serialazition.StaticFireworkEffectSerialization;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
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
     * @return list of {@link ConfigurationSerializable}
     */
    public static List<ConfigurationSerializable> deserializeItemList(List<Map<String, Object>> itemList) {

        List<ConfigurationSerializable> returnVal = new ArrayList<ConfigurationSerializable>();
        for (Map<String, Object> map : itemList) {
            returnVal.add(deserialize(map));
        }
        return returnVal;
    }

    /**
     * Clones an existing serialized map. This is needed because of the immutable return types.
     * @param original map to clone
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

    /**
     * Serializes a {@link ConfigurationSerializable} into a savable byte stream for usage in databases and so on.
     * @param serializable object that should be serialized
     * @return string of bytes containing the serialized map of the input
     */
    public static String toByteStream(ConfigurationSerializable serializable) {

        // create streams
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Map<String, Object> map = serialize(serializable);

            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // write map
            oos.writeObject(map);
            oos.flush();

            // toHexString
            return new HexBinaryAdapter().marshal(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toByteStream(ItemMeta meta) {

        // workaround for not serializable meta
        if(meta instanceof FireworkMeta) {
            return StaticFireworkEffectSerialization.serialize((FireworkMeta)meta);
        }

        return toByteStream(meta);
    }

    /**
     * Deserializes an previously serialized byte string into its original {@link ConfigurationSerializable} object.
     *
     * @param hex string serialized with {@link SerializationUtil#toByteStream(org.bukkit.configuration.serialization.ConfigurationSerializable)}
     * @return deserialized object
     */
    @SuppressWarnings("unchecked")
    public static ConfigurationSerializable fromByteStream(String hex) {

        // create streams
        byte[] bytes = new HexBinaryAdapter().unmarshal(hex);

        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            ObjectInputStream ois = new ObjectInputStream(bais);
            Map<String, Object> map = (Map<String, Object>) ois.readObject();
            return deserialize(map);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ConfigurationSerializable fromByteStream(String hex, Material type) {

        if(type == Material.FIREWORK) {
            return StaticFireworkEffectSerialization.deserialize(hex);
        }

        return fromByteStream(hex);
    }
}
