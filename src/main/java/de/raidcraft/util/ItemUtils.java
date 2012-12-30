package de.raidcraft.util;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;

/**
 * @author Silthus
 */
public final class ItemUtils {

    private ItemUtils() {

    }

    public static Material getItem(String name) {

        try {
            return getItem(Integer.parseInt(name));
        } catch (NumberFormatException e) {
            return Material.getMaterial(name);
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

    public static String getFriendlyName(Material material) {
        return WordUtils.capitalize(material.name().replace("_", " ").toLowerCase());
    }
}
